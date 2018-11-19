package com.example.telegrambot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BotStarter extends TelegramLongPollingBot {
    private static final Logger Logger = LoggerFactory.getLogger(BotStarter.class);
    private Map<Long, UserStatus> userStatusMap = new HashMap<>();
    @Autowired
    private UserCitiesService userCitiesService;

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(getChatId(update));
        message.enableHtml(true);
        if (!userStatusMap.containsKey(getChatId(update))) {
            userStatusMap.put(getChatId(update), UserStatus.READY);
        }
        Logger.info("User message: {}", update.getMessage() != null ? update.getMessage().getText() : update.getCallbackQuery().getData());

        try {
            if (update.getCallbackQuery() != null && update.getCallbackQuery().getData() != null) {
                if (userStatusMap.get(getChatId(update)) == UserStatus.REMOVE_CITY) {
                    userStatusMap.put(getChatId(update), UserStatus.READY);
                    message.setText("Ваши города для проверок: " + userCitiesService.removeCity(getChatId(update), update.getCallbackQuery().getData()));
                } else {
                    return;
                }
            } else {
                createOutputMessage(update, userStatusMap.get(getChatId(update)), message);
            }
        } catch (Exception e) {
            message.setText(e.getLocalizedMessage());
        }
        try {
            Logger.info("Send message: '{}' to {}", message.getText(), message.getChatId());
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private Long getChatId(Update update) {
        return update.getMessage() != null ? update.getMessage().getChatId() : update.getCallbackQuery().getFrom().getId();
    }

    private void createOutputMessage(Update update, UserStatus userStatus, SendMessage message) throws Exception {
        if (userStatus == UserStatus.READY) {
            switch (update.getMessage().getText().split(" ")[0]) {
                case "/start":
                    message.setText(getStartMessage());
                    break;
                case "/my_cities":
                    message.setText("Ваши города для проверок: " + userCitiesService.getCities(update.getMessage().getChatId()));
                    break;
                case "/add_city":
                    userStatusMap.put(update.getMessage().getChatId(), UserStatus.ADD_CITY);
                    message.setText("Введите город, билеты которого хотите проверять");
                    break;
                case "/remove_city":
                    userStatusMap.put(update.getMessage().getChatId(), UserStatus.REMOVE_CITY);
                    message.setText("Выберете город, который хотите удалить");
                    message.setReplyMarkup(createRowButtons(userCitiesService.getCitiesList(update.getMessage().getChatId())));
                    break;
                default:
                    message.setText("Я не знаю такой команды :(");
            }
            return;
        }
        if (userStatus == UserStatus.ADD_CITY) {
            userStatusMap.put(update.getMessage().getChatId(), UserStatus.READY);
            message.setText("Ваши города для проверок: " + userCitiesService.addNewCity(update.getMessage().getChatId(), update.getMessage().getText().split(" ")[0]));
            return;
        }
        if (userStatus == UserStatus.REMOVE_CITY) {
            userStatusMap.put(update.getMessage().getChatId(), UserStatus.READY);
            message.setText("Ваши города для проверок: " + userCitiesService.removeCity(update.getMessage().getChatId(), update.getMessage().getText().split(" ")[0]));
        }
    }

    private InlineKeyboardMarkup createRowButtons(List<String> citiesList) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>(
                citiesList.stream()
                        .map(city -> {
                            InlineKeyboardButton button = new InlineKeyboardButton();
                            button.setCallbackData(city);
                            button.setText(city);
                            return button;
                        })
                        .collect(Collectors.groupingBy(city -> citiesList.indexOf(city.getText()) % 2, Collectors.toList()))
                        .values());

        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
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
