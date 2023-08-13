module com.example.steamofferswithgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.text;
    requires selenium.api;
    requires selenium.firefox.driver;
    requires selenium.support;


    opens com.example.steamofferswithgui to javafx.fxml;
    exports com.example.steamofferswithgui;
}