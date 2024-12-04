package org.example.weather;

import java.time.LocalDateTime;

public class ForecastData {
    private LocalDateTime date;
    private String status;
    private double averageTemperature;
    private double maxTemperature;
    private double minTemperature;
    private String weatherIcon;

    public ForecastData(LocalDateTime date, String status, double averageTemperature, double maxTemperature, double minTemperature, String weatherIcon) {
        this.date = date;
        this.status = status;
        this.averageTemperature = averageTemperature;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.weatherIcon = weatherIcon;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }
}
