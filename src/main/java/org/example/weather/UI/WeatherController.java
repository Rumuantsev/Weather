package org.example.weather.UI;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.weather.data.modals.ForecastData;
import org.example.weather.data.modals.WeatherData;
import org.example.weather.exceptions.CityNotFoundException;
import org.example.weather.data.GeocodingService;
import org.example.weather.data.DatabaseService;
import org.example.weather.data.WeatherService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static javafx.geometry.Pos.CENTER;

public class WeatherController {
    @FXML
    private ComboBox<String> cityComboBox;
    @FXML
    private Label weatherInfoLabel;
    @FXML
    private ImageView weatherIcon;
    @FXML
    private VBox forecastVBox;

    private final WeatherService weatherService = new WeatherService();
    private final DatabaseService databaseService = new DatabaseService();
    private final GeocodingService geocodingService = new GeocodingService();

    @FXML
    public void initialize() {
        String lastCity = databaseService.getLastSearchedCity();
        if (lastCity != null) {
            cityComboBox.setValue(lastCity);
            searchWeather();
        }

        cityComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> suggestCities(newValue));
    }

    @FXML
    public void suggestCities(String query) {
        if (query != null && !query.trim().isEmpty()) {
            geocodingService.suggestCities(query, cityComboBox);
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

                databaseService.saveCityToDatabase(city);
            } catch (CityNotFoundException e) {
                showMassageBox("Error", e.getMessage());
            } catch (Exception e) {
                showMassageBox("Error", "Failed to fetch weather data: " + e.getMessage());
            }
        } else {
            showMassageBox("Error", "Please enter the name of a city.");
        }
    }

    private void displayWeatherInfo(WeatherData weatherData) {
        String weatherInfo = String.format(
                """
                        Температура: %.1f°C
                        Ощущается как: %.1f°C
                        Максимум: %.1f°C
                        Минимум: %.1f°C
                        Влажность: %d%%
                        Давление: %d hPa
                        Скорость ветра: %.1f м/с
                        Направление ветра: %s
                        Облачность: %s
                        Осадки: %.1f мм""",
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
        forecastVBox.getChildren().clear();

        List<ForecastData> dailyForecast = getDailyForecast(forecastDataArray);

        for (ForecastData forecastData : dailyForecast) {
            HBox forecastDetails = new HBox();
            forecastDetails.setSpacing(15);
            forecastDetails.setStyle("-fx-background-color: black; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 10;");
            forecastDetails.setAlignment(CENTER);

            Label dateLabel = new Label(forecastData.getDate().format(DateTimeFormatter.ofPattern("dd.MM")));
            dateLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-alignment: center;");

            VBox detailsVBox = new VBox();
            detailsVBox.setSpacing(5);
            detailsVBox.setAlignment(CENTER);

            Label statusLabel = new Label("Состояние: " + forecastData.getStatus());
            statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

            Label averageTempLabel = new Label(String.format("Средняя температура: %.1f°C", forecastData.getAverageTemperature()));
            averageTempLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

            Label maxTempLabel = new Label(String.format("Максимум: %.1f°C", forecastData.getMaxTemperature()));
            maxTempLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

            Label minTempLabel = new Label(String.format("Минимум: %.1f°C", forecastData.getMinTemperature()));
            minTempLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

            detailsVBox.getChildren().addAll(statusLabel, averageTempLabel, maxTempLabel, minTempLabel);

            String iconUrl = "http://openweathermap.org/img/wn/" + forecastData.getWeatherIcon() + ".png";
            ImageView icon = new ImageView();
            try {
                Image image = new Image(iconUrl, true);
                icon.setImage(image);
                if (image.isError()) {
                    System.out.println("Ошибка загрузки иконки: " + iconUrl);
                }
            } catch (Exception e) {
                System.out.println("Не удалось загрузить иконку погоды: " + iconUrl);
                e.printStackTrace();
                showMassageBox("Ошибка", "Не удалось загрузить иконку погоды: " + e.getMessage());
            }

            icon.setFitWidth(60);
            icon.setFitHeight(60);
            icon.setPreserveRatio(true);

            forecastDetails.getChildren().addAll(dateLabel, detailsVBox, icon);

            forecastVBox.getChildren().add(forecastDetails);

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
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        return java.util.Arrays.stream(forecastDataArray)
                .filter(data -> !data.getDate().toLocalDate().isBefore(tomorrow))
                .collect(Collectors.toMap(
                        data -> data.getDate().toLocalDate(),
                        data -> data,
                        (existing, replacement) -> existing))
                .values()
                .stream()
                .sorted(Comparator.comparing(data -> data.getDate().toLocalDate()))
                .limit(4)
                .collect(Collectors.toList());
    }

    private void showMassageBox(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
