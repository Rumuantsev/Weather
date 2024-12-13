package org.example.weather.usecases;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.weather.domain.CityNotFoundException;
import org.example.weather.domain.ForecastData;
import org.example.weather.domain.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherService {
    private static final String API_KEY = "559a014acf069376bfe96d4085734259";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast";
    public static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    // Метод получения текущей погоды по названию города
    public WeatherData getWeatherByCity(String city) throws CityNotFoundException {
        try {
            // Формируем запрос с названием города
            String urlStr = BASE_URL + "?q=" + URLEncoder.encode(city, "UTF-8") + "&appid=" + API_KEY + "&units=metric";
            JsonObject jsonObject = makeApiCall(urlStr);

            // Извлекаем основные данные о погоде из ответа
            JsonObject main = jsonObject.getAsJsonObject("main");
            double temperature = main.get("temp").getAsDouble();
            double feelsLike = main.get("feels_like").getAsDouble();
            double tempMin = main.get("temp_min").getAsDouble();
            double tempMax = main.get("temp_max").getAsDouble();
            int humidity = main.get("humidity").getAsInt();
            int pressure = main.get("pressure").getAsInt();

            // Извлекаем данные о ветре
            JsonObject wind = jsonObject.getAsJsonObject("wind");
            double windSpeed = wind.get("speed").getAsDouble();
            String windDirection = wind.get("deg").getAsString();

            // Извлекаем иконку погоды и облачность
            JsonArray weatherArray = jsonObject.getAsJsonArray("weather");
            String weatherIcon = weatherArray.get(0).getAsJsonObject().get("icon").getAsString();

            String cloudiness = jsonObject.getAsJsonObject("clouds").get("all").getAsString();
            double precipitation = jsonObject.has("rain") ? jsonObject.getAsJsonObject("rain").get("1h").getAsDouble() : 0;

            // Создаем объект WeatherData с извлеченными данными
            return new WeatherData(temperature, feelsLike, tempMin, tempMax, humidity, pressure, windSpeed, windDirection, weatherIcon, cloudiness, precipitation);
        } catch (Exception e) {
            logger.error("Ошибка получения данных о погоде", e);
            throw new CityNotFoundException("Ошибка при получении данных о погоде.");
        }
    }

    // Метод получения прогноза погоды по названию города
    public ForecastData[] getForecastByCity(String city) throws CityNotFoundException {
        try {
            String urlStr = FORECAST_URL + "?q=" + URLEncoder.encode(city, "UTF-8") + "&appid=" + API_KEY + "&units=metric";
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

                // Получаем иконку погоды
                String weatherIcon = dayData.getAsJsonArray("weather").get(0).getAsJsonObject().get("icon").getAsString();

                // Создаем объект ForecastData с иконкой погоды
                forecastDataArray[i] = new ForecastData(dateTime, status, temp, tempMax, tempMin, weatherIcon);
            }

            return forecastDataArray;
        } catch (Exception e) {
            logger.error("Ошибка получения данных о погоде", e);
            throw new CityNotFoundException("Ошибка при получении данных о погоде.");
        }
    }

    // Метод для выполнения вызова API и обработки ответа
    private JsonObject makeApiCall(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new CityNotFoundException("Город не найден или ошибка API. Код ответа: " + responseCode);
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

