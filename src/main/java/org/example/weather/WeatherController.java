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
        // Загрузить последний искомый город
        String lastCity = databaseUtil.getLastSearchedCity();
        if (lastCity != null) {
            cityComboBox.setValue(lastCity);
            searchWeather(lastCity);
        }

        cityComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            suggestCities(newValue);
        });
    }

    @FXML
    public void suggestCities(String query) {
        if (query != null && !query.trim().isEmpty()) {
            citySuggestionService.suggestCities(query, cityComboBox);
        } else {
            cityComboBox.getItems().clear();
        }
    }

    @FXML
    public void searchWeather() {
        String city = cityComboBox.getValue();
        if (city != null && !city.trim().isEmpty()) {
            searchWeather(city);  // Запросить погоду для введенного города
        } else {
            showAlert("Ошибка", "Пожалуйста, введите название города.");
        }
    }

    private void searchWeather(String city) {
        try {
            WeatherData weatherData = weatherService.getWeatherByCity(city);
            displayWeatherInfo(weatherData);  // Отобразить информацию о погоде
            databaseUtil.saveCityToDatabase(city);  // Сохранить город в базе данных
        } catch (CityNotFoundException e) {
            showAlert("Ошибка", e.getMessage());
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось получить данные о погоде: " + e.getMessage());
        }
    }

    private void displayWeatherInfo(WeatherData weatherData) {
        String weatherInfo = String.format(
                "Температура: %.1f°C\n" +
                        "Ощущается как: %.1f°C\n" +
                        "Макс: %.1f°C\n" +
                        "Мин: %.1f°C\n" +
                        "Влажность: %d%%\n" +
                        "Давление: %d гПа\n" +
                        "Скорость ветра: %.1f м/с\n" +
                        "Направление ветра: %s\n" +
                        "Облачность: %s\n" +
                        "Осадки: %.1f мм",
                weatherData.getTemperature(),
                weatherData.getFeelsLike(),
                weatherData.getTempMax(),
                weatherData.getTempMin(),
                weatherData.getHumidity(),
                weatherData.getPressure(),
                weatherData.getWindSpeed(),
                weatherData.getWindDirection(),
                weatherData.getCloudiness(),
                weatherData.getPrecipitation());  // Предполагается, что эти методы существуют

        weatherInfoLabel.setText(weatherInfo);  // Устанавливаем текст для label

        // Загружаем иконку погоды
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
