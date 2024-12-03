package org.example.weather;

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

    // Сеттеры
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public void setCloudiness(String cloudiness) {
        this.cloudiness = cloudiness;  // Сеттер для облачности
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;  // Сеттер для осадков
    }
}
