package org.example.weather.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.weather.exceptions.CityNotFoundException;
import org.example.weather.data.modals.ForecastData;
import org.example.weather.data.modals.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherService {
    private static final String API_KEY = "1d31c8d847b58f0c97b3d05540103ad9";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";

    public static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    public WeatherData getWeatherByCity(String city) throws CityNotFoundException {
        try {
            String urlStr = BASE_URL + "/weather" + "?lang=en&q=" + URLEncoder.encode(city, StandardCharsets.UTF_8) + "&appid=" + API_KEY + "&units=metric";
            JsonObject jsonObject = makeApiCall(urlStr);

            JsonObject main = jsonObject.getAsJsonObject("main");
            double temperature = main.get("temp").getAsDouble();
            double feelsLike = main.get("feels_like").getAsDouble();
            double tempMin = main.get("temp_min").getAsDouble();
            double tempMax = main.get("temp_max").getAsDouble();
            int humidity = main.get("humidity").getAsInt();
            int pressure = main.get("pressure").getAsInt();

            JsonObject wind = jsonObject.getAsJsonObject("wind");
            double windSpeed = wind.get("speed").getAsDouble();
            String windDirection = wind.get("deg").getAsString();

            JsonArray weatherArray = jsonObject.getAsJsonArray("weather");
            String weatherIcon = weatherArray.get(0).getAsJsonObject().get("icon").getAsString();

            String cloudiness = jsonObject.getAsJsonObject("clouds").get("all").getAsString();
            double precipitation = jsonObject.has("rain") ? jsonObject.getAsJsonObject("rain").get("1h").getAsDouble() : 0;

            return new WeatherData(temperature, feelsLike, tempMin, tempMax, humidity, pressure, windSpeed, windDirection, weatherIcon, cloudiness, precipitation);
        } catch (Exception e) {
            logger.error("Error retrieving weather data", e);
            throw new CityNotFoundException("Error retrieving weather data.", e);
        }
    }

    public ForecastData[] getForecastByCity(String city) throws CityNotFoundException {
        try {
            String urlStr = BASE_URL + "/forecast" + "?lang=en&q=" + URLEncoder.encode(city, StandardCharsets.UTF_8) + "&appid=" + API_KEY + "&units=metric";
            JsonObject jsonObject = makeApiCall(urlStr);

            JsonArray list = jsonObject.getAsJsonArray("list");
            ForecastData[] forecastDataArray = new ForecastData[list.size()];

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < list.size(); i++) {
                JsonObject dayData = list.get(i).getAsJsonObject();
                String date = dayData.get("dt_txt").getAsString();
                LocalDateTime dateTime = LocalDateTime.parse(date, formatter);

                String status = dayData.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
                double temp = dayData.getAsJsonObject("main").get("temp").getAsDouble();
                double tempMax = dayData.getAsJsonObject("main").get("temp_max").getAsDouble();
                double tempMin = dayData.getAsJsonObject("main").get("temp_min").getAsDouble();

                String weatherIcon = dayData.getAsJsonArray("weather").get(0).getAsJsonObject().get("icon").getAsString();

                forecastDataArray[i] = new ForecastData(dateTime, status, temp, tempMax, tempMin, weatherIcon);
            }

            return forecastDataArray;
        } catch (Exception e) {
            logger.error("Error retrieving forecast data", e);
            throw new CityNotFoundException("Error retrieving forecast data.", e);
        }
    }

    private JsonObject makeApiCall(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new CityNotFoundException("City not found or API error. Response code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return JsonParser.parseString(response.toString()).getAsJsonObject();
    }
}
