package com.example.telegrambot.model.user_cities;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface UserCitiesRepository extends JpaRepository<UserCities, Long> {
    @Override
    List<UserCities> findAll();

    List<UserCities> findAllByUserId(Long id);

    boolean existsByUserIdAndCityName(Long id, String city);

    @Transactional
    List<UserCities> deleteByUserIdAndCityNameContainingIgnoreCase(Long id, String city);

}
