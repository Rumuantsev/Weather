package org.example.weather;

import java.sql.*;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:postgresql://localhost:5433/java";
    private static final String USER = "postgres";
    private static final String PASSWORD = "251925";

    public void saveCityToDatabase(String city) {
        String normalizedCity = city.toLowerCase(); // Преобразуем имя города в нижний регистр
        String insertQuery = "INSERT INTO weather (name) VALUES (?) ON CONFLICT (name) DO NOTHING";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            System.out.println("Подключение к базе данных успешно.");
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, normalizedCity);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Город успешно добавлен в базу данных: " + city);
                } else {
                    System.out.println("Город уже существует в базе данных или не был добавлен: " + city);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL при сохранении города в базу данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getLastSearchedCity() {
        String selectQuery = "SELECT name FROM weather ORDER BY id DESC LIMIT 1";
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            System.out.println("Подключение к базе данных для получения последнего города успешно.");  // Логирование подключения
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке последнего города из базы данных: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Метод для проверки подключения к базе данных
    public boolean testDatabaseConnection() {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            System.out.println("Успешное подключение к базе данных");
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
