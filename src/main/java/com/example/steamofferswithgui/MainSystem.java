package com.example.steamofferswithgui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.steamofferswithgui.Format.*;
import static com.example.steamofferswithgui.NodeManagement.ANALYSED_ITEM_COLUMN;
import static com.example.steamofferswithgui.NodeManagement.ANALYSED_ITEM_ROW;

public class MainSystem {

    public static final String PAGE_PREFIX = "#p";
    public static int NUMBER_OF_PAGES;
    public static String SORTING = "_quantity_desc";
    public static String STATIC_URL_PART;
    public static String SEARCH_FILTER;
    public static final int ITEMS_ON_PAGE = 10;
    public static final int ONE_NEXT_INDEX = 1;
    public static final int ACCEPTED_ITEM_COLUMN = 1;
    public static final String FIRST_PAGE_INDEX = "#p1";
    public static final int SECOND_PAGE_INDEX = 2;
    public static int acceptedItemRowCounter = 2;


    public static void startSteamOffersSystem(String itemType, String searchFilter) throws IOException, InterruptedException {
        ItemsLinks.initItemsLinks();
        STATIC_URL_PART = getStaticUrlPart(itemType);
        if (isNotEmpty(searchFilter)){
            SEARCH_FILTER = "&q=";
        }
        searchFilter = searchFilter.replace(" ", "+");
        SEARCH_FILTER += searchFilter;
        HtmlRequests.initPageWebDriver();
        String url = STATIC_URL_PART + SEARCH_FILTER + FIRST_PAGE_INDEX + SORTING;
        String pageHtml = serveGettingPageHtml(url);

        for (int currentPageIndex = SECOND_PAGE_INDEX; currentPageIndex <= NUMBER_OF_PAGES; currentPageIndex++){
            List <Integer> itemsDataStartIndexes = new ArrayList<>();
            List <Integer> itemsDataEndIndexes = new ArrayList<>();
            getStartAndEndHeaderIndex(pageHtml, itemsDataStartIndexes, itemsDataEndIndexes);
            calculateItemFromPage(pageHtml, itemsDataStartIndexes, itemsDataEndIndexes);

            url = STATIC_URL_PART + SEARCH_FILTER + PAGE_PREFIX + currentPageIndex + SORTING;
            pageHtml = serveGettingPageHtml(url);

        }
        HtmlRequests.pageDriver.close();
    }

    private static String serveGettingPageHtml(String url) throws InterruptedException {
        String pageHtml;
        pageHtml = HtmlRequests.getHtml(url, HtmlType.PAGE);
        NUMBER_OF_PAGES = getCurrentItemTypePages(pageHtml);
        while (HtmlRequests.couldNotGetPages()) {
            HtmlRequests.refreshPage();
            HtmlRequests.waitForHtmlLoad(HtmlType.PAGE);
            NUMBER_OF_PAGES = getCurrentItemTypePages(pageHtml);
        }
        return pageHtml;
    }

    private static boolean isNotEmpty(String searchFilter) {
        return !Objects.equals(searchFilter, "");
    }

    private static String getStaticUrlPart(String itemType) {
        return ItemsLinks.getLinksToItemsByName().get(itemType);
    }

    private static void calculateItemFromPage(String pageHtml, List<Integer> itemsDataStartIndexes, List<Integer> itemsDataEndIndexes) throws InterruptedException {
        for (int itemIndex = 0; itemIndex < ITEMS_ON_PAGE; itemIndex++) {
            String currentItemHeader = createItemHeader(pageHtml, itemsDataStartIndexes, itemsDataEndIndexes, itemIndex);
            int dataAppid = getItemAppid(currentItemHeader);
            String dataHashName = getItemDataHashName(currentItemHeader);

            NodeManagement.checkAndDeleteItemLabel("ANALYSED_ITEM");
            ImageView loading = NodeManagement.getLoadingWheelImage();

            Label itemLabel = NodeManagement.createItemLabel("Analysed item: " + dataHashName, ItemLabelType.IN_PROGRESS);

            NodeManagement.updateInfoWheelToLoading(loading, itemLabel);
            Platform.runLater(() -> {
                SteamOffersGui.getMainPane().add(itemLabel, ANALYSED_ITEM_COLUMN, ANALYSED_ITEM_ROW);
            });

            try {
                Item currentItem = new Item(dataAppid, dataHashName);
                currentItem = makeItemOnceMoreIfFailed(dataAppid, dataHashName, currentItem);
                checkItemProfit(currentItem);


            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private static Item makeItemOnceMoreIfFailed(int dataAppid, String dataHashName, Item currentItem) throws IOException, InterruptedException {
        while (couldNotCreateItem(currentItem)){
            currentItem = new Item(dataAppid, dataHashName);
        }
        return currentItem;
    }

    private static boolean couldNotCreateItem(Item currentItem) {
        return currentItem.getDataHashName().equals("INVALID");
    }

    private static void checkItemProfit(Item currentItem) throws InterruptedException {
        int points = currentItem.analyseItem();
        if (itemHavePotential(points)){
            Label acceptedItemLabel = NodeManagement.createItemLabel(currentItem.getDataHashName() + " have: " + points + " points", ItemLabelType.ACCEPTED);
            Platform.runLater(() -> {
                try {
                    NodeManagement.changeLoadingWheelToAcceptWheel();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                SteamOffersGui.getMainPane().add(acceptedItemLabel, ACCEPTED_ITEM_COLUMN, acceptedItemRowCounter);
                acceptedItemRowCounter++;
            });
            System.out.println(currentItem.getDataHashName() + ", punkty opłacalnośći: " + points);
        } else {
            Platform.runLater(() -> {
                try {
                    NodeManagement.changeLoadingWheelToFDenyWheel();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }

    private static boolean itemHavePotential(int points) {
        return points > 0;
    }

    private static String createItemHeader(String pageHtml, List<Integer> itemsDataStartIndexes, List<Integer> itemsDataEndIndexes, int itemIndex) {

        try {
            return pageHtml.substring(itemsDataStartIndexes.get(itemIndex), itemsDataEndIndexes.get(itemIndex));
        } catch (Exception e){
            return null;
        }
    }

    private static int getStartAndEndHeaderIndex(String pageHtml, List<Integer> itemsDataStartIndexes, List<Integer> itemsDataEndIndexes) {
        for (int currentItemIndex = 0; currentItemIndex < ITEMS_ON_PAGE; currentItemIndex++){

            itemsDataStartIndexes.add(pageHtml.indexOf("\"result_" + currentItemIndex));
            try {
                String itemDataHtmlPart = pageHtml.substring(itemsDataStartIndexes.get(currentItemIndex));
                itemsDataEndIndexes.add(itemDataHtmlPart.indexOf(">") + itemsDataStartIndexes.get(currentItemIndex) + ONE_NEXT_INDEX);
            } catch (Exception e) {
                return -1;
            }

        }
        return 0;
    }
}