package com.example.steamofferswithgui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Objects;

public class NodeManagement {
    public static final int ANALYSED_ITEM_FONT_SIZE = 20;
    public static final int LOADING_WHEEL_COLUMN = 0;
    public static final int LOADING_WHEEL_ROW = 0;
    public static final int ANALYSED_ITEM_COLUMN = 1;
    public static final int ANALYSED_ITEM_ROW = 0;
    public static final int DECISION_WHEEL_SHOW_TIME = 1000;

    public static Label createItemLabel(String itemLabelText, ItemLabelType labelType) {
        Label itemLabel = new Label(itemLabelText);
        itemLabel.setId("ANALYSED_ITEM");
        if (labelType == ItemLabelType.IN_PROGRESS){
            itemLabel.setTextFill(Color.GREEN);
        } else if (labelType == ItemLabelType.ACCEPTED){
            itemLabel.setTextFill(Color.RED);
        }
        itemLabel.setFont(new Font(ANALYSED_ITEM_FONT_SIZE));
        return itemLabel;
    }

    public static ImageView getLoadingWheelImage() {
        ImageView loading = SteamOffersGui.initInfoWheel("/steamOffersImages/loading.gif");
        SteamOffersGui.getMainPane().setAlignment(Pos.TOP_LEFT);
        loading.setId("LOADING_WHEEL");
        return loading;
    }

    public static ImageView getDenyWheelImage() {
        ImageView loading = SteamOffersGui.initInfoWheel("/steamOffersImages/denied.gif");
        SteamOffersGui.getMainPane().setAlignment(Pos.TOP_LEFT);
        loading.setId("DENIED_WHEEL");
        return loading;
    }

    public static ImageView getAcceptWheelImage() {
        ImageView loading = SteamOffersGui.initInfoWheel("/steamOffersImages/accepted.gif");
        SteamOffersGui.getMainPane().setAlignment(Pos.TOP_LEFT);
        loading.setId("ACCEPTED_WHEEL");
        return loading;
    }

    public static Node findNodeById(String id){
        for (Node element : SteamOffersGui.getMainPane().getChildren()){
            if (Objects.equals(element.getId(), id)){
                return element;
            }
        }
        return null;
    }

    public static void checkAndDeleteItemLabel(String id) throws InterruptedException {
        Node element = findNodeById(id);
        if (element != null){
            Platform.runLater(() -> {
                SteamOffersGui.getMainPane().getChildren().remove(element);
            });
        }
    }

    public static void changeLoadingWheelToFDenyWheel() throws InterruptedException {
        NodeManagement.checkAndDeleteItemLabel("LOADING_WHEEL");
        ImageView denyImage = NodeManagement.getDenyWheelImage();
        SteamOffersGui.getMainPane().add(denyImage, LOADING_WHEEL_COLUMN, LOADING_WHEEL_ROW);
    }

    public static void changeLoadingWheelToAcceptWheel() throws InterruptedException {
        NodeManagement.checkAndDeleteItemLabel("LOADING_WHEEL");
        ImageView acceptImage = NodeManagement.getAcceptWheelImage();
        SteamOffersGui.getMainPane().add(acceptImage, LOADING_WHEEL_COLUMN, LOADING_WHEEL_ROW);
    }

    public static void updateInfoWheelToLoading(ImageView loading, Label itemLabel) {
        Platform.runLater(() -> {
            Node element = findNodeById("DENIED_WHEEL");
            if (element != null){
                try {
                    checkAndDeleteItemLabel("DENIED_WHEEL");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                SteamOffersGui.getMainPane().add(loading, LOADING_WHEEL_COLUMN, LOADING_WHEEL_ROW);
            } else {
                try {
                    checkAndDeleteItemLabel("ACCEPTED_WHEEL");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                SteamOffersGui.getMainPane().add(loading, LOADING_WHEEL_COLUMN, LOADING_WHEEL_ROW);
            }
            try {
                Thread.sleep(DECISION_WHEEL_SHOW_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
