package com.example.receiverpubsub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/receiveMessage")
    public String receiveMessage() {
        return "Hello World";
    }
}
