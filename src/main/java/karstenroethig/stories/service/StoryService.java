package karstenroethig.stories.service;

import java.util.Collection;

import karstenroethig.stories.dto.StoryListDto;
import karstenroethig.stories.dto.StoryShowDto;
import karstenroethig.stories.dto.StoryWordsDto;

public interface StoryService
{
	public StoryShowDto findStory( String key );

	public Collection<StoryListDto> getAllStories();

	public StoryWordsDto countStoryWords( String key );
}
