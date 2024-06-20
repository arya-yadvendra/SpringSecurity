package com.arya.SpringSecurityApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("hello")
    public String greet() {
        return "Hello User! ";
    }

    @GetMapping("about")
    public String about(HttpServletRequest request) {
        return "Session ID : "+request.getSession().getId();
    }
}