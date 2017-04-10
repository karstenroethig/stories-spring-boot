package karstenroethig.stories.controller;

import org.springframework.context.annotation.ComponentScan;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@ComponentScan
@Controller
public class HomeController {

    @RequestMapping(
        value = "/",
        method = RequestMethod.GET
    )
    public String home( Model model ) {
        return "redirect:/story/list";
    }
}
