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
            String itemType = getItemTypeFromItemsComboBox();
            String searchFilter = getSearchFilterFromTextField();
            if (itemTypeIsNotSelected(itemType)){
                return;
            }
            clearMainPane();

            Thread searchTask = new Thread(() -> {
                try {
                    MainSystem.startSteamOffersSystem(itemType, searchFilter);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            searchTask.start();
        });
    }

    private static String getSearchFilterFromTextField() {
        return SteamOffersGui.getNameSearch().getText();
    }

    private static boolean itemTypeIsNotSelected(String itemType) {
        return itemType == null;
    }

    private static String getItemTypeFromItemsComboBox() {
        return (String) SteamOffersGui.getItemsComboBox().getValue();
    }


    private static void clearMainPane() {
        SteamOffersGui.getMainPane().getChildren().clear();
    }


}
