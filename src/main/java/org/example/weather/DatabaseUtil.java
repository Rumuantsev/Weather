package org.example.weather;

import java.io.*;
import java.util.Properties;

public class DatabaseUtil {
    private static final String SETTINGS_FILE = "settings.properties";

    public void saveCity(String city) {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            Properties properties = new Properties();
            properties.setProperty("lastCity", city);
            properties.store(fos, null);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении города: " + e.getMessage());
        }
    }

    public String getLastSearchedCity() {
        try (FileInputStream fis = new FileInputStream(SETTINGS_FILE)) {
            Properties properties = new Properties();
            properties.load(fis);
            return properties.getProperty("lastCity");
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке города: " + e.getMessage());
        }
        return null;
    }
}
