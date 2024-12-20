package org.example.weather.exceptions;


public class CityNotFoundException extends Exception {
    public CityNotFoundException(String message) {
        super(message);
    }
    public CityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

