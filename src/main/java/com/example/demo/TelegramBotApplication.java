package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

@SpringBootApplication
public class TelegramBotApplication {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
        String host = "0.0.0.0";
        int port = Integer.valueOf(System.getenv("PORT"));
        botOptions.setProxyPort(port);
        botOptions.setProxyHost(host);
        SpringApplication.run(TelegramBotApplication.class, args);
    }

}
