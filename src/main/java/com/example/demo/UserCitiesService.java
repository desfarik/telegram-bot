package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserCitiesService {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(UserCitiesService.class);
    private Map<Long, Set<String>> userCities;
    private static final String CITY_DELIMITER = ", ";

    public String getCities(Long chatId) {
        return userCities.get(chatId).size() == 0 ? "Бездна в океане" : String.join(CITY_DELIMITER, userCities.get(chatId));
    }

    public String addNewCity(Long chatId, String city) {
        if (userCities.containsKey(chatId)) {
            Set<String> oldCities = userCities.get(chatId);
            oldCities.add(city.toLowerCase());
            userCities.put(chatId, oldCities);
        } else {
            userCities.put(chatId, new HashSet<>(Collections.singletonList(city)));
        }
        saveUserCities();
        Logger.info("Add new city {} for user with id={}, All CITIES:{}", city, chatId, userCities.get(chatId));
        return getCities(chatId);
    }

    public String removeCity(Long chatId, String city) {
        if (userCities.containsKey(chatId)) {
            Set<String> oldCities = userCities.get(chatId);
            oldCities.remove(city.toLowerCase());
            userCities.put(chatId, oldCities);
        } else {
            Logger.info("Current user has'nt city:{}", city);
            return "Бездна в океане";
        }
        saveUserCities();
        Logger.info("Remove city {} for user with id={}, All CITIES:{}", city, chatId, userCities.get(chatId));
        return getCities(chatId);
    }

    @PostConstruct
    private void readUserCities() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectReader reader = mapper.reader(HashMap.class);
            userCities = ((Map<String, ArrayList<String>>) reader.readValue(new File("./user-cities.json"))).entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> Long.valueOf(e.getKey()), e -> new HashSet<>(e.getValue())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUserCities() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("./user-cities.json"), userCities);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
