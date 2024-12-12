package org.example.weather.domain;

public class WeatherData {
    private double temperature;
    private double feelsLike;
    private double tempMin;
    private double tempMax;
    private int humidity;
    private int pressure;
    private double windSpeed;
    private String windDirection;
    private String weatherIcon;
    private String cloudiness;  // Облачность
    private double precipitation;  // Осадки

    // Конструктор
    public WeatherData(double temperature, double feelsLike, double tempMin, double tempMax,
                       int humidity, int pressure, double windSpeed,
                       String windDirection, String weatherIcon, String cloudiness, double precipitation) {
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.weatherIcon = weatherIcon;
        this.cloudiness = cloudiness;
        this.precipitation = precipitation;
    }

    // Геттеры
    public double getTemperature() {
        return temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public double getTempMin() {
        return tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public String getCloudiness() {
        return cloudiness;  // Геттер для облачности
    }

    public double getPrecipitation() {
        return precipitation;  // Геттер для осадков
    }
}
