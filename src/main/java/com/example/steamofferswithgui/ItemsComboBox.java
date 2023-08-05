package com.example.steamofferswithgui;

import javafx.scene.control.ComboBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;

public class ItemsComboBox<String> extends ComboBox {
    public static final java.lang.String TEXTBOX_BACKGROUND_HEX = "#2a475e";

    public ItemsComboBox(){
        super();
        this.setBackground(new Background(new BackgroundFill(Paint.valueOf(TEXTBOX_BACKGROUND_HEX), null, null)));
        this.setPromptText("Choose items to search");
        this.getItems().add("★ StatTrack™");
    }
}
