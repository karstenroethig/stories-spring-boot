package karstenroethig.stories.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import karstenroethig.stories.controller.exceptions.NotFoundException;
import karstenroethig.stories.dto.StoryWordsDto;
import karstenroethig.stories.service.StoryService;


@RestController
@RequestMapping( "/rest/1.0/stories" )
public class RestStoryController {

    @Autowired
    StoryService storyService;

    @RequestMapping(
        value = "/{key}/words",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<StoryWordsDto> words( @PathVariable( "key" ) String storyKey ) {

        StoryWordsDto data = storyService.countStoryWords( storyKey );

        if( data == null ) {
            throw new NotFoundException( storyKey );
        }

        return new ResponseEntity<StoryWordsDto>( data, HttpStatus.OK );
    }

    @ExceptionHandler( NotFoundException.class )
    void handleNotFoundException( HttpServletResponse response, NotFoundException ex ) throws IOException {
        response.sendError( HttpStatus.NOT_FOUND.value(),
            String.format( "Story %s does not exist.", ex.getMessage() ) );
    }
}
