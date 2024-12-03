package org.example.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherService {
    private static final String API_KEY = "559a014acf069376bfe96d4085734259";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    public static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    public WeatherData getWeatherByCity(String city) throws CityNotFoundException {
        try {
            String urlStr = BASE_URL + "?q=" + URLEncoder.encode(city, "UTF-8") + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();
                throw new CityNotFoundException("City not found: " + city + " - Response: " + errorResponse.toString());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject main = jsonObject.getAsJsonObject("main");
            double temperature = main.get("temp").getAsDouble();
            double feelsLike = main.get("feels_like").getAsDouble();
            double tempMin = main.get("temp_min").getAsDouble();
            double tempMax = main.get("temp_max").getAsDouble();
            int humidity = main.get("humidity").getAsInt();
            int pressure = main.get("pressure").getAsInt();

            JsonObject wind = jsonObject.getAsJsonObject("wind");
            double windSpeed = wind.get("speed").getAsDouble();
            String windDirection = wind.get("deg").getAsString(); // Преобразуйте в сторону ветра, если нужно

            JsonArray weatherArray = jsonObject.getAsJsonArray("weather");
            String weatherIcon = weatherArray.get(0).getAsJsonObject().get("icon").getAsString();

            WeatherData weatherData = new WeatherData(temperature, feelsLike, tempMin, tempMax, humidity, pressure, windSpeed, windDirection, weatherIcon);
            return weatherData;

        } catch (Exception e) {
            logger.error("Error getting weather data", e);
            throw new CityNotFoundException("Error fetching weather data");
        }
    }

}
