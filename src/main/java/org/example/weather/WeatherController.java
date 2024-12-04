package org.example.weather;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox; // Импорт HBox вместо VBox
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WeatherController {
    @FXML
    private ComboBox<String> cityComboBox;
    @FXML
    private Label weatherInfoLabel;
    @FXML
    private ImageView weatherIcon;
    @FXML
    private HBox forecastHBox; // Изменено на HBox

    private WeatherService weatherService = new WeatherService();
    private DatabaseUtil databaseUtil = new DatabaseUtil();
    private CitySuggestionService citySuggestionService = new CitySuggestionService();

    @FXML
    public void initialize() {
        String lastCity = databaseUtil.getLastSearchedCity();
        if (lastCity != null) {
            cityComboBox.setValue(lastCity);
            searchWeather();
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
            try {
                WeatherData weatherData = weatherService.getWeatherByCity(city);
                displayWeatherInfo(weatherData);

                ForecastData[] forecastDataArray = weatherService.getForecastByCity(city);
                displayForecast(forecastDataArray);

                databaseUtil.saveCityToDatabase(city);
            } catch (CityNotFoundException e) {
                showAlert("Ошибка", e.getMessage());
            } catch (Exception e) {
                showAlert("Ошибка", "Не удалось получить данные о погоде: " + e.getMessage());
            }
        } else {
            showAlert("Ошибка", "Пожалуйста, введите название города.");
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
                weatherData.getPrecipitation());

        weatherInfoLabel.setText(weatherInfo);

        String iconUrl = "http://openweathermap.org/img/wn/" + weatherData.getWeatherIcon() + "@2x.png";
        Image image = new Image(iconUrl);
        weatherIcon.setImage(image);
    }

    private void displayForecast(ForecastData[] forecastDataArray) {
        forecastHBox.getChildren().clear(); // Очистка HBox перед добавлением новых элементов

        List<ForecastData> dailyForecast = getDailyForecast(forecastDataArray);

        for (ForecastData forecastData : dailyForecast) {
            VBox forecastDetails = new VBox();
            forecastDetails.setSpacing(5);
            forecastDetails.setStyle("-fx-background-color: #444444; -fx-padding: 10; -fx-background-radius: 5;");

            // Форматирование даты
            Label dateLabel = new Label(forecastData.getDate().format(DateTimeFormatter.ofPattern("dd.MM")));
            dateLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            // Статус
            Label statusLabel = new Label("Статус: " + forecastData.getStatus());
            statusLabel.setStyle("-fx-text-fill: white;");

            // Средняя, максимальная и минимальная температуры
            Label averageTempLabel = new Label(String.format("Средняя температура: %.1f°C", forecastData.getAverageTemperature()));
            averageTempLabel.setStyle("-fx-text-fill: white;");

            Label maxTempLabel = new Label(String.format("Макс: %.1f°C", forecastData.getMaxTemperature()));
            maxTempLabel.setStyle("-fx-text-fill: white;");

            Label minTempLabel = new Label(String.format("Мин: %.1f°C", forecastData.getMinTemperature()));
            minTempLabel.setStyle("-fx-text-fill: white;");

            // Иконка
            String iconUrl = "http://openweathermap.org/img/wn/" + forecastData.getWeatherIcon() + "@2x.png";
            ImageView icon = new ImageView();
            try {
                Image image = new Image(iconUrl, true); // true для async загрузки
                icon.setImage(image);
                if (image.isError()) {
                    System.out.println("Ошибка загрузки иконки: " + iconUrl);
                }
            } catch (Exception e) {
                System.out.println("Не удалось загрузить иконку: " + iconUrl);
                e.printStackTrace(); // Вывод ошибки в консоль
                showAlert("Ошибка", "Не удалось загрузить иконку погоды: " + e.getMessage());
            }

            icon.setFitWidth(50);
            icon.setFitHeight(50);
            icon.setPreserveRatio(true);

            // Добавление всех элементов в VBox
            forecastDetails.getChildren().addAll(dateLabel, statusLabel, averageTempLabel, maxTempLabel, minTempLabel, icon);

            // Добавление VBox в HBox
            forecastHBox.getChildren().add(forecastDetails);

            // Добавление информации о прогнозе в виде строки
            System.out.println(forecastDataToString(forecastData));
        }
    }

    private String forecastDataToString(ForecastData forecastData) {
        return String.format("%s: %s, %.1f°C (%.1f°C / %.1f°C)",
                forecastData.getDate().format(DateTimeFormatter.ofPattern("dd.MM")),
                forecastData.getStatus(),
                forecastData.getAverageTemperature(),
                forecastData.getMaxTemperature(),
                forecastData.getMinTemperature());
    }

    private List<ForecastData> getDailyForecast(ForecastData[] forecastDataArray) {
        LocalDate tomorrow = LocalDate.now().plusDays(1); // Завтрашний день

        // Получаем прогнозы на завтрашний день и следующие три дня
        List<ForecastData> forecastList = java.util.Arrays.stream(forecastDataArray)
                .filter(data -> !data.getDate().toLocalDate().isBefore(tomorrow)) // Прогноз на завтра и позже
                .collect(Collectors.toMap(
                        data -> data.getDate().toLocalDate(), // Ключ - дата прогноза
                        data -> data, // Значение - сам прогноз
                        (existing, replacement) -> existing)) // Если есть дубликаты, оставляем первый
                .values()
                .stream()
                .sorted(Comparator.comparing(data -> data.getDate().toLocalDate())) // Сортируем по возрастанию даты
                .limit(4) // Ограничиваем результат первыми четырьмя днями
                .collect(Collectors.toList());

        return forecastList;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
