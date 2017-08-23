package karstenroethig.stories.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import karstenroethig.stories.dto.ChapterDto;
import karstenroethig.stories.dto.StoryListDto;
import karstenroethig.stories.dto.StoryShowDto;
import karstenroethig.stories.dto.StoryWordsDto;
import karstenroethig.stories.service.StoryService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StoryServiceImpl implements StoryService
{
	@Override
	public List<StoryListDto> getAllStories()
	{
		List<StoryListDto> stories = new ArrayList<StoryListDto>();

		Path path = Paths.get( "data/stories.json" );

		// exists?
		if ( Files.notExists( path ) )
		{
			log.warn( path.toAbsolutePath().toString() + " does not exist." );

			return stories;
		}

		// read content
		String content = null;

		try
		{
			byte[] bytes = Files.readAllBytes( path );
			content = new String( bytes, StandardCharsets.UTF_8 );
		}
		catch ( IOException ex )
		{
			log.error( "error reading file " + path.toString(), ex );

			return stories;
		}

		JSONArray jsonStories = new JSONArray( content );

		for ( int i = 0; i < jsonStories.length(); i++ )
		{
			JSONObject jsonStory = jsonStories.getJSONObject( i );
			StoryListDto story = new StoryListDto();

			story.setTitle( jsonStory.getString( "title" ) );
			story.setLanguage( jsonStory.getString( "language" ) );
			story.setPlot( jsonStory.getString( "plot" ) );

			story.setKey( generateKey( story.getTitle() ) );

			stories.add( story );
		}

		Collections.sort( stories, new Comparator<StoryListDto>()
		{
			@Override
			public int compare( StoryListDto o1, StoryListDto o2 )
			{
				return o1.getTitle().compareTo( o2.getTitle() );
			}
		} );

		return stories;
	}

	@Override
	public StoryShowDto findStory( String key )
	{
		// read content
		String content = findStoryContent( key );

		if ( StringUtils.isBlank( content ) )
		{
			return null;
		}

		JSONObject jsonStory = new JSONObject( content );
		StoryShowDto story = new StoryShowDto();

		story.setTitle( jsonStory.getString( "title" ) );
		story.setSubtitle( readOptionalString( jsonStory, "subtitle", null ) );
		story.setComment( readOptionalString( jsonStory, "comment", null ) );

		if ( jsonStory.has( "paragraphs" ) )
		{
			JSONArray jsonParagraphs = jsonStory.getJSONArray( "paragraphs" );

			for ( int i = 0; i < jsonParagraphs.length(); i++ )
			{
				String paragraph = jsonParagraphs.getString( i );

				story.addParagraph( paragraph );
			}
		}

		if ( jsonStory.has( "chapters" ) )
		{
			JSONArray jsonChapters = jsonStory.getJSONArray( "chapters" );

			for ( int i = 0; i < jsonChapters.length(); i++ )
			{
				JSONObject jsonChapter = jsonChapters.getJSONObject( i );
				ChapterDto chapter = new ChapterDto();

				chapter.setTitle( jsonChapter.getString( "title" ) );
				chapter.setSubtitle( readOptionalString( jsonChapter, "subtitle", null ) );
				chapter.setComment( readOptionalString( jsonChapter, "comment", null ) );

				if ( jsonChapter.has( "paragraphs" ) )
				{
					JSONArray jsonParagraphs = jsonChapter.getJSONArray( "paragraphs" );

					for ( int j = 0; j < jsonParagraphs.length(); j++ )
					{
						String paragraph = jsonParagraphs.getString( j );

						chapter.addParagraph( paragraph );
					}
				}

				chapter.setKey( generateKey( chapter.getTitle() ) );

				story.addChapter( chapter );
			}
		}

		return story;
	}

	@Override
	public StoryWordsDto countStoryWords( String key )
	{
		// read content
		String content = findStoryContent( key );

		if ( StringUtils.isBlank( content ) )
		{
			return null;
		}

		// count all the words
		int words = countWords( content );

		StoryWordsDto data = new StoryWordsDto();

		data.setWords( NumberFormat.getInstance().format( words ) );

		return data;
	}

	private String findStoryContent( String key )
	{
		Path path = Paths.get( "data/stories/" + key + ".json" );

		// exists?
		if ( Files.notExists( path ) )
		{
			log.error( path.toAbsolutePath().toString() + " does not exist." );

			return null;
		}

		// read content
		try
		{
			byte[] bytes = Files.readAllBytes( path );

			return new String( bytes, StandardCharsets.UTF_8 );
		}
		catch ( IOException ex )
		{
			log.error( "error reading file " + path.toString(), ex );
		}

		return null;
	}

	private String readOptionalString( JSONObject jsonObject, String key, String defaultValue )
	{
		if ( jsonObject == null || StringUtils.isBlank( key ) || jsonObject.has( key ) == false )
		{
			return defaultValue;
		}

		return jsonObject.getString( key );
	}

	public static String generateKey( String oldKey )
	{
		String newKey = StringUtils.replaceChars( oldKey, ' ', '_' );
		newKey = StringUtils.replaceChars( newKey, ".", "_" );
		newKey = StringUtils.replaceChars( newKey, "ß", "ss" );
		newKey = StringUtils.replaceChars( newKey, "ä", "ae" );
		newKey = StringUtils.replaceChars( newKey, "ü", "ue" );
		newKey = StringUtils.replaceChars( newKey, "ö", "oe" );
		newKey = StringUtils.replaceChars( newKey, "Ä", "Ae" );
		newKey = StringUtils.replaceChars( newKey, "Ü", "Ue" );
		newKey = StringUtils.replaceChars( newKey, "Ö", "Oe" );

		return newKey;
	}

	private static int countWords( String text )
	{
		int wordCount = 0;

		boolean word = false;
		int endOfLine = text.length() - 1;

		for ( int i = 0; i < text.length(); i++ )
		{
			// if the char is a letter, word = true.
			if ( Character.isLetter( text.charAt( i ) ) && i != endOfLine )
			{
				word = true;
			}
			// if char isn't a letter and there have been letters before, counter goes up.
			else if ( Character.isLetter( text.charAt( i ) ) == false && word )
			{
				wordCount++;
				word = false;
			}
			// last word of String; if it doesn't end with a non letter, it wouldn't count without this.
			else if ( Character.isLetter( text.charAt( i ) ) && i == endOfLine )
			{
				wordCount++;
			}
		}

		return wordCount;
	}
}
