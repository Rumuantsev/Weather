package org.example.weather.usecases;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CitySuggestionService {
    private static final String API_KEY = "559a014acf069376bfe96d4085734259";
    private static final String GEOCODING_URL = "http://api.openweathermap.org/geo/1.0/direct";
    private final Set<String> suggestedCities = new HashSet<>();

    public void suggestCities(String query, ComboBox<String> cityComboBox) {
        if (query == null || query.isEmpty()) {
            // Если запрос пустой, очищаем элементы ComboBox
            cityComboBox.getItems().clear();
            return;
        }

        new Thread(() -> {
            try {
                String urlStr = GEOCODING_URL + "?q=" + query + "&limit=5&appid=" + API_KEY;
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();

                if (responseCode != 200) {
                    return; // Обработка ошибок, если необходимо
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
                List<String> newSuggestions = new ArrayList<>();

                if (jsonArray.size() == 0) {
                    // Если массив пуст, значит город не найден, очищаем список
                    Platform.runLater(() -> {
                        cityComboBox.getItems().clear();
                    });
                    return;
                }

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject cityObj = jsonArray.get(i).getAsJsonObject();
                    String cityName = cityObj.get("name").getAsString();
                    String country = cityObj.has("country") ? cityObj.get("country").getAsString() : "";
                    String fullCity = cityName + (country.isEmpty() ? "" : ", " + country);

                    // Проверяем, если город уже добавлен в список предложений
                    if (!suggestedCities.contains(fullCity)) {
                        newSuggestions.add(fullCity);
                        suggestedCities.add(fullCity);
                    }
                }

                // Обновляем ComboBox в JavaFX потоке
                Platform.runLater(() -> {
                    cityComboBox.getItems().clear();
                    cityComboBox.getItems().addAll(newSuggestions);
                });

            } catch (Exception e) {
                e.printStackTrace(); // Обработка исключений по мере необходимости
            }
        }).start();
    }
}
