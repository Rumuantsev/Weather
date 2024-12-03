module org.example.weather {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.slf4j;
    requires java.sql;
    requires java.desktop;

    opens org.example.weather to javafx.fxml;
    exports org.example.weather;
}
