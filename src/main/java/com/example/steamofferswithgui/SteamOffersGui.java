package com.example.steamofferswithgui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
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
    public static final int MAIN_PANE_TOP_PADDING = 30;
    public static final int MAIN_PANE_RIGHT_PADDING = 10;
    public static final int MAIN_PANE_DOWN_PADDING = 0;
    public static final int MAIN_PANE_LEFT_PADDING = 30;
    public static final int LOADING_IMAGE_HEIGHT = 50;
    public static final int LOADING_IMAGE_WIDTH = 50;
    public static final int NAME_SEARCH_COLUMN_INDEX = 0;
    public static final int NAME_SEARCH_ROW_INDEX = 2;
    private SearchOffersButton startLooking;
    private static GridPane mainPane;
    private static ItemsComboBox<String> possibleSearchOptions;
    private static TextField nameSearch;

    @Override
    public void start(Stage stage) throws IOException {
        mainPane = initMenuPane();
        mainPane.setBackground(new Background(new BackgroundFill(Paint.valueOf(BACKGROUND_COLOR_HEX), null, null)));
        ScrollPane mainContainer = initMainContainer();
        Scene scene = new Scene(mainContainer, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setTitle("Steam market offers by czisiasty - v1.0");
        stage.setScene(scene);
        stage.show();
        initMenu(mainPane);
    }

    private static ScrollPane initMainContainer() {
        ScrollPane mainContainer = new ScrollPane(mainPane);
        mainContainer.setFitToWidth(true);
        mainContainer.setFitToHeight(true);
        return mainContainer;
    }

    private static GridPane initMenuPane() {
        GridPane mainPane = new GridPane();
        mainPane.setMinWidth(SCENE_WIDTH);
        mainPane.setAlignment(Pos.TOP_CENTER);
        mainPane.setPadding(new Insets(MAIN_PANE_TOP_PADDING, MAIN_PANE_RIGHT_PADDING, MAIN_PANE_DOWN_PADDING, MAIN_PANE_LEFT_PADDING));
        return mainPane;
    }

    public void initMenu(GridPane mainPane){
        ImageView steamOffersLogo = generateImage("/steamOffersImages/steamOffersLogo.png");
        mainPane.add(steamOffersLogo, LOGO_COLUMN_INDEX, LOGO_ROW_INDEX);
        possibleSearchOptions = new ItemsComboBox<>();
        mainPane.add(possibleSearchOptions, COMBOBOX_COLUMN_INDEX, COMBOBOX_ROW_INDEX);
        startLooking = new SearchOffersButton("Search");
        mainPane.add(startLooking, BUTTON_COLUMN_INDEX, BUTTON_ROW_INDEX);
        nameSearch = new TextField();
        mainPane.add(nameSearch, NAME_SEARCH_COLUMN_INDEX, NAME_SEARCH_ROW_INDEX);

    }

    public static ImageView generateImage(String path){
        Image image = new Image(SteamOffersGui.class.getResourceAsStream(path));
        return new ImageView(image);
    }

    public static GridPane getMainPane(){
        return mainPane;
    }

    public static ItemsComboBox<String> getItemsComboBox(){
        return possibleSearchOptions;
    }

    public static void main(String[] args) {
        System.setProperty("webdriver.gecko.driver", "C:\\Program Files\\geckodriver\\geckodriver.exe");
        launch();
    }

    public static ImageView initInfoWheel(String path) {
        ImageView loading = SteamOffersGui.generateImage(path);
        loading.setFitHeight(LOADING_IMAGE_HEIGHT);
        loading.setFitWidth(LOADING_IMAGE_WIDTH);
        return loading;
    }

    public static TextField getNameSearch(){
        return nameSearch;
    }

    public static ItemsComboBox<String> getPossibleSearchOptions(){
        return possibleSearchOptions;
    }
}