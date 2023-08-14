package com.example.steamofferswithgui;

import javafx.scene.control.ComboBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;

public class ItemsComboBox<String> extends ComboBox {
    public static final java.lang.String TEXTBOX_BACKGROUND_HEX = "#2a475e";
    public static final java.lang.String STAT_TRACK_KNIFE_NAME = "★ StatTrack™";
    public static final java.lang.String KNIFE_AND_GLOVES_NAME = "★";
    public static final java.lang.String GLOVES_NAME = "★ Gloves";

    public ItemsComboBox(){
        super();
        this.setBackground(new Background(new BackgroundFill(Paint.valueOf(TEXTBOX_BACKGROUND_HEX), null, null)));
        initItemsComboBox();

    }

    private void initItemsComboBox() {
        this.setPromptText("Choose items to search");
        this.getItems().add(STAT_TRACK_KNIFE_NAME);
        this.getItems().add(KNIFE_AND_GLOVES_NAME);
        this.getItems().add(GLOVES_NAME);
    }
}
