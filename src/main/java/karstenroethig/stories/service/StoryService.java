package karstenroethig.stories.service;

import java.util.Collection;

import karstenroethig.stories.dto.StoryListDto;
import karstenroethig.stories.dto.StoryShowDto;


public interface StoryService {

    public StoryShowDto findStory( String key );

    public Collection<StoryListDto> getAllStories();
}
