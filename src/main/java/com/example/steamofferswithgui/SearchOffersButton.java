package com.example.steamofferswithgui;

import javafx.scene.Cursor;
import javafx.scene.control.Button;

public class SearchOffersButton extends Button {

    public SearchOffersButton(String buttonText){
        super(buttonText);
        this.setOnMouseEntered(mouseEvent -> {
            this.setCursor(Cursor.HAND);
        });
    }

}
