package com.example.steamofferswithgui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;

public class SteamOffersGui extends Application {

    public static final int SCENE_WIDTH = 1280;
    public static final int SCENE_HEIGHT = 720;
    public static final String BACKGROUND_COLOR_HEX = "#171a21";
    public static final int LOGO_COLUMN_INDEX = 0;
    public static final int LOGO_ROW_INDEX = 0;
    public static final int COMBOBOX_COLUMN_INDEX = 0;
    public static final int COMBOBOX_ROW_INDEX = 1;
    public static final int BUTTON_COLUMN_INDEX = 1;
    public static final int BUTTON_ROW_INDEX = 1;
    public static final String TEXT_COLOR_HEX = "#fff";
    private SearchOffersButton startLooking;

    @Override
    public void start(Stage stage) throws IOException {
        GridPane mainPane = initMenuPane();
        mainPane.setBackground(new Background(new BackgroundFill(Paint.valueOf(BACKGROUND_COLOR_HEX), null, null)));
        Scene scene = new Scene(mainPane, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setTitle("Steam market offers by czisiasty - v1.0");
        stage.setScene(scene);
        stage.show();
        initMenu(mainPane);
    }

    private static GridPane initMenuPane() {
        GridPane mainPane = new GridPane();
        mainPane.setMinWidth(SCENE_WIDTH);
        mainPane.setAlignment(Pos.TOP_CENTER);
        return mainPane;
    }

    public void initMenu(GridPane mainPane){
        ImageView steamOffersLogo = generateImage("/steamOffersImages/steamOffersLogo.png");
        mainPane.add(steamOffersLogo, LOGO_COLUMN_INDEX, LOGO_ROW_INDEX);
        ItemsComboBox<String> possibleSearchOptions = new ItemsComboBox();
        mainPane.add(possibleSearchOptions, COMBOBOX_COLUMN_INDEX, COMBOBOX_ROW_INDEX);
        startLooking = new SearchOffersButton("Search");
        mainPane.add(startLooking, BUTTON_COLUMN_INDEX, BUTTON_ROW_INDEX);
    }

    public ImageView generateImage(String path){
        Image image = new Image(getClass().getResourceAsStream(path));
        return new ImageView(image);
    }

    public static void main(String[] args) {
        launch();
    }
}