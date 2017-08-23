package karstenroethig.stories.service.impl;

import karstenroethig.stories.dto.ChapterDto;
import karstenroethig.stories.dto.StoryShowDto;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONObject;

import org.springframework.stereotype.Service;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class ConverterServiceImpl
{
	@PostConstruct
	public void convert()
	{
		final Path convertDirectory = Paths.get( "data/convert" );

		if ( Files.exists( convertDirectory ) == false )
		{
			log.info( "conversion skipped (directory 'data/convert' does not exsist)" );

			return;
		}

		try
		{
			Files.walkFileTree( convertDirectory, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException
				{
					if ( StringUtils.endsWith( file.toString(), ".txt" ) == false )
					{
						return FileVisitResult.CONTINUE;
					}

					List<StoryShowDto> stories = readStoriesFromFile( file );

					writeStoriesToJson( convertDirectory, stories );

					return FileVisitResult.CONTINUE;
				}
			} );
		}
		catch ( IOException ex )
		{
			log.error( "error in conversion", ex );
		}
	}

	private List<StoryShowDto> readStoriesFromFile( Path file ) throws IOException
	{
		log.info( "reading file " + file.toString() );

		List<StoryShowDto> stories = new ArrayList<StoryShowDto>();

		StoryShowDto story = null;
		ChapterDto chapter = null;

		for ( String line : Files.readAllLines( file, StandardCharsets.UTF_8 ) )
		{
			String filteredLine = filterLine( line );

			if ( StringUtils.isBlank( filteredLine ) )
			{
				continue;
			}

			// new chapter
			if ( StringUtils.startsWith( filteredLine, "==" ) )
			{
				if ( ( chapter != null ) && ( story != null ) )
				{
					story.addChapter( chapter );
				}

				chapter = new ChapterDto();

				chapter.setTitle( filterLine( StringUtils.replace( filteredLine, "==", StringUtils.EMPTY ) ) );

				continue;
			}

			// new story
			if ( StringUtils.startsWith( filteredLine, "=" ) )
			{
				if ( story != null )
				{
					if ( chapter != null )
					{
						story.addChapter( chapter );
					}

					stories.add( story );
				}

				story = new StoryShowDto();
				chapter = null;

				story.setTitle( filterLine( StringUtils.replace( filteredLine, "=", StringUtils.EMPTY ) ) );

				continue;
			}

			// new paragraph
			if ( chapter != null )
			{
				chapter.addParagraph( filteredLine );
			}
			else if ( story != null )
			{
				story.addParagraph( filteredLine );
			}
		}

		if ( story != null )
		{
			if ( chapter != null )
			{
				story.addChapter( chapter );
			}

			stories.add( story );
		}

		log.info( stories.size() + " stories found" );

		return stories;
	}

	private String filterLine( String line )
	{
		String filtered = StringUtils.trimToEmpty( line );

		filtered = StringUtils.replaceChars( filtered, '„', '"' );
		filtered = StringUtils.replaceChars( filtered, '“', '"' );

		return filtered;
	}

	private void writeStoriesToJson( Path outputDirectory, List<StoryShowDto> stories ) throws IOException
	{
		if ( stories.isEmpty() )
		{
			return;
		}

		for ( StoryShowDto story : stories )
		{
			String filename = generateFilename( story.getTitle(), -1, ".json" );

			log.info( "creating file " + filename );

			Path newFile = Paths.get( outputDirectory.toString(), filename );
			int counter = 1;

			while ( Files.exists( newFile ) )
			{
				newFile = Paths.get( outputDirectory.toString(), generateFilename( story.getTitle(), ++counter, ".json" ) );
			}

			if ( counter > 1 )
			{
				log.info( "-> " + generateFilename( story.getTitle(), counter, ".json" ) );
			}

			Files.createFile( newFile );

			// create json file
			JSONObject jsonStory = new JSONObject();

			jsonStory.put( "title", story.getTitle() );

			if ( ( story.getParagraphs() != null ) && ( story.getParagraphs().isEmpty() == false ) )
			{
				jsonStory.put( "paragraphs", story.getParagraphs() );
			}

			if ( ( story.getChapters() != null ) && ( story.getChapters().isEmpty() == false ) )
			{
				List<JSONObject> jsonChapters = new ArrayList<>();

				for ( ChapterDto chapter : story.getChapters() )
				{
					JSONObject jsonChapter = new JSONObject();

					jsonChapter.put( "title", chapter.getTitle() );

					if ( ( chapter.getParagraphs() != null ) && ( chapter.getParagraphs().isEmpty() == false ) )
					{
						jsonChapter.put( "paragraphs", chapter.getParagraphs() );
					}

					jsonChapters.add( jsonChapter );
				}

				jsonStory.put( "chapters", jsonChapters );
			}

			Files.write( newFile, jsonStory.toString( 2 ).getBytes( StandardCharsets.UTF_8 ) );
		}
	}

	private String generateFilename( String title, int counter, String ending )
	{
		StringBuffer filename = new StringBuffer();

		filename.append( StoryServiceImpl.generateKey( title ) );

		if ( counter > 0 )
		{
			filename.append( "(" );
			filename.append( counter );
			filename.append( ")" );
		}

		filename.append( ending );

		return filename.toString();
	}
}
