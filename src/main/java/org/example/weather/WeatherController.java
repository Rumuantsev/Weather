package org.example.weather;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WeatherController {
    @FXML
    private ComboBox<String> cityComboBox;
    @FXML
    private Label weatherInfoLabel;
    @FXML
    private ImageView weatherIcon;

    private WeatherService weatherService = new WeatherService();
    private DatabaseUtil databaseUtil = new DatabaseUtil();
    private CitySuggestionService citySuggestionService = new CitySuggestionService();

    @FXML
    public void initialize() {
        if (!databaseUtil.testDatabaseConnection()) {
            showAlert("Ошибка", "Не удалось подключиться к базе данных.");
            return;
        }

        try {
            String lastCity = databaseUtil.getLastSearchedCity();
            if (lastCity != null) {
                cityComboBox.setValue(lastCity);
                searchWeather(lastCity);
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось загрузить последний город.");
        }
    }

    @FXML
    public void suggestCities() {
        String query = cityComboBox.getEditor().getText();
        if (query != null && !query.trim().isEmpty()) {
            citySuggestionService.suggestCities(query, cityComboBox);
        }
    }

    @FXML
    public void searchWeather() {
        String city = cityComboBox.getValue();
        if (city != null && !city.trim().isEmpty()) {
            searchWeather(city);
        } else {
            showAlert("Ошибка", "Пожалуйста, введите название города.");
        }
    }

    private void searchWeather(String city) {
        try {
            WeatherData weatherData = weatherService.getWeatherByCity(city);
            displayWeatherInfo(weatherData);
            databaseUtil.saveCityToDatabase(city);
            System.out.println("Погода получена и город сохранен: " + city);
        } catch (CityNotFoundException e) {
            showAlert("Ошибка", e.getMessage());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось получить данные о погоде: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayWeatherInfo(WeatherData weatherData) {
        String weatherInfo = String.format("Температура: %.1f°C\nОщущается как: %.1f°C\nМакс: %.1f°C\nМин: %.1f°C\nВлажность: %d%%\nДавление: %d гПа\nСкорость ветра: %.1f м/с\nНаправление ветра: %s",
                weatherData.getTemperature(), weatherData.getFeelsLike(), weatherData.getTempMax(),
                weatherData.getTempMin(), weatherData.getHumidity(), weatherData.getPressure(),
                weatherData.getWindSpeed(), weatherData.getWindDirection());
        weatherInfoLabel.setText(weatherInfo);

        String iconUrl = "http://openweathermap.org/img/wn/" + weatherData.getWeatherIcon() + "@2x.png";
        Image image = new Image(iconUrl);
        weatherIcon.setImage(image);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle(title);
        alert.showAndWait();
    }
}