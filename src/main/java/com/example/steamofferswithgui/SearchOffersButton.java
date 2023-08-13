package com.example.steamofferswithgui;

import javafx.scene.Cursor;
import javafx.scene.control.Button;

import java.io.IOException;


public class SearchOffersButton extends Button {


    public SearchOffersButton(String buttonText){
        super(buttonText);
        this.setOnMouseEntered(mouseEvent -> {
            this.setCursor(Cursor.HAND);
        });

        this.setOnMouseClicked(mouseEvent -> {
            clearMainPane();

            Thread searchTask = new Thread(() -> {
                try {
                    MainSystem.startSteamOffersSystem();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            searchTask.start();
        });
    }


    private static void clearMainPane() {
        SteamOffersGui.getMainPane().getChildren().clear();
    }


}
