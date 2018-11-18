package com.example.telegrambot.model;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface UserCitiesRepository extends JpaRepository<UserCities, Long> {
    @Override
    List<UserCities> findAll();

    List<UserCities> findAllByUserId(Long id);

    boolean existsByUserIdAndCity(Long id, String city);

    @Transactional
    List<UserCities> deleteByUserIdAndCity(Long id, String city);

}
