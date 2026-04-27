package com.college.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class HomeController {

    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("/web");
    }

    @GetMapping("/api")
    public String apiRoot() {
        return "API Root. <br> " +
               "Available endpoints:<br>" +
               "- <a href='/api/students'>/api/students</a><br>" +
               "- <a href='/api/events'>/api/events</a>";
    }
}
