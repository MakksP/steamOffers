package com.example.steamofferswithgui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;

public class SteamOffersGui extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        GridPane mainPane = new GridPane();
        mainPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("#171a21"), null, null)));
        Scene scene = new Scene(mainPane, 1280, 720);
        stage.setTitle("Steam market offers by czisiasty - v1.0");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}