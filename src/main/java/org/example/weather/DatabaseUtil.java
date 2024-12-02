package org.example.weather;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Импортируем SQLException

public class DatabaseUtil {
    private static final String URL = "jdbc:postgresql://localhost:5433/weather";
    private static final String USER = "postgres";
    private static final String PASSWORD = "251925";

    public void saveCity(String city) throws SQLException { // Изменяем исключение на SQLException
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO cities (name) VALUES (?) ON CONFLICT (name) DO NOTHING";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, city);
                statement.executeUpdate();
            }
        }
    }

    public String getLastSearchedCity() throws SQLException { // Изменяем исключение на SQLException
        String lastCity = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT name FROM cities ORDER BY id DESC LIMIT 1"; // Предполагая, что 'id' является первичным ключом
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    lastCity = resultSet.getString("name");
                }
            }
        }
        return lastCity;
    }
}
