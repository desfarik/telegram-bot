package com.example.telegrambot;

import com.example.telegrambot.model.UserCities;
import com.example.telegrambot.model.UserCitiesRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCitiesService {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(UserCitiesService.class);
    private static final String CITY_DELIMITER = ", ";

    @Autowired
    private UserCitiesRepository userCitiesRepository;

    public String getCities(Long chatId) {
        List<UserCities> userCities = userCitiesRepository.findAllByUserId(chatId);

        return userCities.size() == 0 ? "Бездна в океане" :
                String.join(CITY_DELIMITER, userCities.stream().map(UserCities::getCity).collect(Collectors.toList()));
    }

    public String addNewCity(Long chatId, String city) {
        if (!userCitiesRepository.existsByUserIdAndCity(chatId, city.toLowerCase())) {
            UserCities userCities = new UserCities();
            userCities.setUserId(chatId);
            userCities.setCity(city.toLowerCase());
            userCitiesRepository.save(userCities);
        }
        Logger.info("Add new city {} for user with id={}", city, chatId);
        return getCities(chatId);
    }

    public String removeCity(Long chatId, String city) {
        userCitiesRepository.deleteByUserIdAndCity(chatId, city.toLowerCase());
        Logger.info("Remove city {} for user with id={}", city, chatId);
        return getCities(chatId);
    }
}
