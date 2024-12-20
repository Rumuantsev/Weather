package org.example.weather;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WeatherApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WeatherApplication.class.getResource("weather.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 900);
        stage.setTitle("Weather app");
        stage.setScene(scene);

        stage.setMinWidth(400);
        stage.setMinHeight(900);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
