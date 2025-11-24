package com.example.word.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WordController {
    @GetMapping("/hello")
    public String hello(){
        return "Hello World";
    }
}
