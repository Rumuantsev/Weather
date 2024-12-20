module org.example.weather {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.slf4j;
    requires java.sql;
    requires java.desktop;

    opens org.example.weather to javafx.fxml;
    exports org.example.weather;
    exports org.example.weather.data;
    opens org.example.weather.data to javafx.fxml;
    exports org.example.weather.UI;
    opens org.example.weather.UI to javafx.fxml;
    exports org.example.weather.data.modals;
    opens org.example.weather.data.modals to javafx.fxml;
    exports org.example.weather.exceptions;
    opens org.example.weather.exceptions to javafx.fxml;
}
