package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class FakeRestController {

    @GetMapping("/")
    public String getAnswer() {
        return "telegram bot for Dima";
    }
}