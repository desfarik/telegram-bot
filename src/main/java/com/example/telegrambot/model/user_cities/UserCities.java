package com.example.telegrambot.model.user_cities;


import com.example.telegrambot.model.city.City;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "UserCities")
public class UserCities {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long userId;
    @OneToOne
    private City city;
}
