package karstenroethig.stories.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@Setter
@ToString
public class StoryListDto
{
	private String key;
	private String title;
	private String language;
	private String plot;
}
