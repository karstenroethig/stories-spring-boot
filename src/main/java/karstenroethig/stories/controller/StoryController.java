package karstenroethig.stories.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import karstenroethig.stories.controller.exceptions.NotFoundException;
import karstenroethig.stories.dto.StoryShowDto;
import karstenroethig.stories.service.StoryService;


@ComponentScan
@Controller
@RequestMapping( "/story" )
public class StoryController {

    @Autowired
    StoryService storyService;

    @RequestMapping(
        value = "/list",
        method = RequestMethod.GET
    )
    public String list( Model model ) {

        model.addAttribute( "allStories", storyService.getAllStories() );

        return "views/story/list";
    }

    @RequestMapping(
        value = "/show/{key}",
        method = RequestMethod.GET
    )
    public String show( @PathVariable( "key" ) String storyKey, Model model ) {

        StoryShowDto story = storyService.findStory( storyKey );

        if( story == null ) {
            throw new NotFoundException( storyKey );
        }

        model.addAttribute( "story", story );

        return "views/story/show";
    }

    @ExceptionHandler( NotFoundException.class )
    void handleNotFoundException( HttpServletResponse response, NotFoundException ex ) throws IOException {
        response.sendError( HttpStatus.NOT_FOUND.value(),
            String.format( "Story %s does not exist.", ex.getMessage() ) );
    }
}
