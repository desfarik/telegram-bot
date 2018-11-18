package com.example.telegrambot;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FakeRestController {

    @RequestMapping("/")
    public String getAnswer() {
        return "<p style=\"text-align: center\">telegram bot for Dima</p>";
    }
}