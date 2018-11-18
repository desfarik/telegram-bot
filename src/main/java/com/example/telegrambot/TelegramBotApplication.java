package com.example.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableJpaAuditing
public class TelegramBotApplication {

    public static void main(String[] args)
    {
        ApiContextInitializer.init();
        SpringApplication.run(TelegramBotApplication.class, args);
    }
}
