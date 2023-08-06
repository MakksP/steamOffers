package com.example.steamofferswithgui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static com.example.steamofferswithgui.MainSystem.THREADS_COUNT;

public class SearchOffersButton extends Button {

    public static final int LOADING_IMAGE_HEIGHT = 50;
    public static final int LOADING_IMAGE_WIDTH = 50;

    public SearchOffersButton(String buttonText){
        super(buttonText);
        this.setOnMouseEntered(mouseEvent -> {
            this.setCursor(Cursor.HAND);
        });

        this.setOnMouseClicked(mouseEvent -> {
            clearMainPane();
            ImageView loading = initLoadingWheel();
            SteamOffersGui.getMainPane().setAlignment(Pos.TOP_LEFT);
            SteamOffersGui.getMainPane().add(loading, 0, 0);

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

    private static ImageView initLoadingWheel() {
        ImageView loading = SteamOffersGui.generateImage("/steamOffersImages/loading.gif");
        loading.setFitHeight(LOADING_IMAGE_HEIGHT);
        loading.setFitWidth(LOADING_IMAGE_WIDTH);
        return loading;
    }

    private static void clearMainPane() {
        SteamOffersGui.getMainPane().getChildren().clear();
    }


}
