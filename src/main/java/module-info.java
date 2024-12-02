module org.example.weather {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.slf4j;
    requires lombok;
    requires java.sql;
    requires java.desktop; // Добавлено для использования java.awt

    opens org.example.weather to javafx.fxml;
    exports org.example.weather;
}
