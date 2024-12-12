package org.example.weather.domain;

public class CityNotFoundException extends Exception {
    public CityNotFoundException(String message) {
        super(message);
    }
}
