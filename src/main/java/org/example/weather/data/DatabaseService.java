package org.example.weather.data;

import java.sql.*;

public class DatabaseService {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/weatherdb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public void saveCityToDatabase(String city) {
        String normalizedCity = city.toLowerCase();
        String insertQuery = "INSERT INTO public.city (name) VALUES (?);";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            System.out.println("Successfully connected to the database.");
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, normalizedCity);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("City successfully added to the database: " + city);
                } else {
                    System.out.println("City already exists in the database or was not added: " + city);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error while saving the city to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getLastSearchedCity() {
        String selectQuery = "SELECT name FROM city ORDER BY id DESC LIMIT 1";
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            System.out.println("Successfully connected to the database to retrieve the last city.");
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving the last city from the database: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
