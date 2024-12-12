module org.example.weather {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.slf4j;
    requires java.sql;
    requires java.desktop;

    opens org.example.weather to javafx.fxml;
    exports org.example.weather;
    exports org.example.weather.domain;
    opens org.example.weather.domain to javafx.fxml;
    exports org.example.weather.usecases;
    opens org.example.weather.usecases to javafx.fxml;
    exports org.example.weather.controllers;
    opens org.example.weather.controllers to javafx.fxml;
}
