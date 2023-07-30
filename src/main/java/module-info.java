module com.example.steamofferswithgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.text;


    opens com.example.steamofferswithgui to javafx.fxml;
    exports com.example.steamofferswithgui;
}