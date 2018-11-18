package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
public class BotStarter extends TelegramLongPollingBot {
    private static final Logger Logger = LoggerFactory.getLogger(BotStarter.class);
    private Map<Long, UserStatus> userStatusMap = new HashMap<>();
    @Autowired
    private UserCitiesService userCitiesService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() == null || update.getMessage().getChatId() == null) {
            return;
        }
        if (!userStatusMap.containsKey(update.getMessage().getChatId())) {
            userStatusMap.put(update.getMessage().getChatId(), UserStatus.READY);
        }
        Logger.info("User message: {}", update.getMessage().getText());
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        message.enableHtml(true);
        message.setText(createOutputMessage(update, userStatusMap.get(update.getMessage().getChatId())));
        try {
            Logger.info("Send message: '{}' to {}", message.getText(), message.getChatId());
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String createOutputMessage(Update update, UserStatus userStatus) {
        if (userStatus == UserStatus.READY) {
            switch (update.getMessage().getText().split(" ")[0]) {
                case "/start":
                    return getStartMessage();
                case "/my_cities":
                    return "Ваши города для проверок: " + userCitiesService.getCities(update.getMessage().getChatId());
                case "/add_city":
                    userStatusMap.put(update.getMessage().getChatId(), UserStatus.ADD_CITY);
                    return "Введите город, билеты которого хотите проверять";
                case "/remove_city":
                    userStatusMap.put(update.getMessage().getChatId(), UserStatus.REMOVE_CITY);
                    return "Введите город, который хотите удалить";
                default:
                    return "Я не знаю такой команды :(";
            }
        }
        if (userStatus == UserStatus.ADD_CITY) {
            userStatusMap.put(update.getMessage().getChatId(), UserStatus.READY);
            return "Ваши города для проверок: " + userCitiesService.addNewCity(update.getMessage().getChatId(), update.getMessage().getText().split(" ")[0]);
        }
        if (userStatus == UserStatus.REMOVE_CITY) {
            userStatusMap.put(update.getMessage().getChatId(), UserStatus.READY);
            return "Ваши города для проверок: " + userCitiesService.removeCity(update.getMessage().getChatId(), update.getMessage().getText().split(" ")[0]);
        }
        return "Что-то пошло не так!!!";
    }

    private String getStartMessage() {
        return "<b>Добрый день</b>, этот бот написан по приколу за вечер.\n\n" +
                "<b>Основная цель бота:</b> первым узнать о дешевых билетах <a href=\"https://belavia.by/\">Belavia</a> в интересующий город.\n\n" +
                "<b>Основные команды бота:</b>\n" +
                "/add_city - Добавить город для проверки билетов\n" +
                "/remove_city - Удалить город из проверок\n" +
                "/my_cities - Мои города для проверок\n";
    }

    @Override
    public String getBotUsername() {
        return "dima_astreyko_bot";
    }

    @Override
    public String getBotToken() {
        return "748979902:AAFSenZmJIJxmKP_qt2pvRaYz6mLyckbLrQ";
    }
}
