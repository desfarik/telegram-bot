package com.example.telegrambot.model.city;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "City")
@AllArgsConstructor
@NoArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String countryCode;

    public City(String name, String countryCode, String countryId) {
        this.name = name;
        this.countryCode = countryCode;
        this.countryId = countryId;
    }

    private String countryId;
}
