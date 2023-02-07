module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires jmh.core;


    opens org.example to javafx.fxml;
    exports org.example;
}