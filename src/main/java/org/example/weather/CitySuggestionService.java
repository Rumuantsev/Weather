package org.example.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.control.ComboBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CitySuggestionService {
    private static final String API_KEY = "559a014acf069376bfe96d4085734259"; // Замените на ваш API ключ

    public void suggestCities(String query, ComboBox<String> cityComboBox) {
        if (query.isEmpty()) return; // Если строка пустая, ничего не делать

        String urlStr = "http://api.openweathermap.org/data/2.5/find?q=" + query + "&appid=" + API_KEY + "&limit=5";
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JsonArray cityList = JsonParser.parseString(response.toString()).getAsJsonObject().getAsJsonArray("list");
                cityComboBox.getItems().clear(); // Очистить предыдущие предложения
                for (int i = 0; i < cityList.size(); i++) {
                    JsonObject city = cityList.get(i).getAsJsonObject();
                    String cityName = city.get("name").getAsString();
                    cityComboBox.getItems().add(cityName); // Добавить город в ComboBox
                }
            }
        } catch (Exception e) {
            WeatherService.logger.error("Ошибка при получении подсказок по городам", e);
        }
    }
}
