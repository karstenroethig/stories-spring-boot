package karstenroethig.stories.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@Setter
@ToString
public class StoryShowDto
{
	private String title;
	private String subtitle;
	private String comment;
	private List<String> paragraphs = new ArrayList<String>();
	private List<ChapterDto> chapters = new ArrayList<ChapterDto>();

	public void addParagraph( String paragraph )
	{
		paragraphs.add( paragraph );
	}

	public void addChapter( ChapterDto chapter )
	{
		chapters.add( chapter );
	}
}
