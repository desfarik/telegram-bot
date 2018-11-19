package com.example.telegrambot;

import com.example.telegrambot.model.city.City;
import com.example.telegrambot.model.city.CityRepository;
import com.example.telegrambot.model.user_cities.UserCities;
import com.example.telegrambot.model.user_cities.UserCitiesRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCitiesService {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(UserCitiesService.class);
    private static final String CITY_DELIMITER = ", ";

    @Autowired
    private UserCitiesRepository userCitiesRepository;
    @Autowired
    private CityRepository cityRepository;

    public String getCities(Long chatId) {
        List<UserCities> userCities = userCitiesRepository.findAllByUserId(chatId);

        return userCities.size() == 0 ? "Бездна в океане" :
                String.join(CITY_DELIMITER, userCities.stream().map(cities -> cities.getCity().getName()).collect(Collectors.toList()));
    }

    public List<String> getCitiesList(Long chatId) {
        return userCitiesRepository.findAllByUserId(chatId).stream().map(cities -> cities.getCity().getName()).collect(Collectors.toList());
    }

    @Transactional
    public String addNewCity(Long chatId, String city) throws Exception {
        City cityObj = cityRepository.getCityByNameContainingIgnoreCase(city);
        if (cityObj == null) {
            throw new Exception("У Белавия нету рейсов в " + city);
        }
        if (userCitiesRepository.existsByUserIdAndCityName(chatId, cityObj.getName())) {
            throw new Exception(city + " уже есть в списке проверяемых городов");
        }
        UserCities userCities = new UserCities();
        userCities.setUserId(chatId);
        userCities.setCity(cityObj);
        userCitiesRepository.save(userCities);
        Logger.info("Add new city {} for user with id={}", city, chatId);
        return getCities(chatId);
    }

    public String removeCity(Long chatId, String city) throws Exception {
        List<UserCities> deletedUserCities = userCitiesRepository.deleteByUserIdAndCityNameContainingIgnoreCase(chatId, city);
        if (deletedUserCities.isEmpty()) {
            throw new Exception(city + " нету в вашем списке проверяемых городов");
        }
        Logger.info("Remove city {} for user with id={}", city, chatId);
        return getCities(chatId);
    }
}
