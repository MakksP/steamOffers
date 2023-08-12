package com.example.steamofferswithgui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.steamofferswithgui.Format.*;
import static com.example.steamofferswithgui.NodeManagement.ANALYSED_ITEM_COLUMN;
import static com.example.steamofferswithgui.NodeManagement.ANALYSED_ITEM_ROW;
import org.openqa.selenium.support.ui.WebDriverWait;


public class MainSystem {

    public static int NUMBER_OF_PAGES;
    public static final String SORTING = "_popular_desc";
    public static final String STATIC_URL_PART = "https://steamcommunity.com/market/search?q=&category_730_ItemSet%5B%5D=any&category_730_ProPlayer%5B%5D=any&category_730_StickerCapsule%5B%5D=any&category_730_TournamentTeam%5B%5D=any&category_730_Weapon%5B%5D=any&category_730_Quality%5B%5D=tag_unusual_strange&appid=730#p";
    public static final int ITEMS_ON_PAGE = 10;
    public static final int ONE_NEXT_INDEX = 1;
    public static final int ACCEPTED_ITEM_COLUMN = 1;
    public static final int FIRST_PAGE_INDEX = 1;
    public static final int SECOND_PAGE_INDEX = 2;
    public static final int MAX_LOAD_SITE_TIME = 10;
    public static int acceptedItemRowCounter = 2;

    public static FirefoxOptions options;
    public static WebDriver driver;

    public static void startSteamOffersSystem() throws IOException, InterruptedException {
        initWebDriver();
        String url = STATIC_URL_PART + FIRST_PAGE_INDEX + SORTING;
        driver.get(url);
        waitForSiteLoad();
        System.out.println(((JavascriptExecutor) driver).executeScript("return document.readyState"));
        String pageHtml = driver.getPageSource();
        NUMBER_OF_PAGES = getCurrentItemTypePages(pageHtml);
        for (int currentPageIndex = SECOND_PAGE_INDEX; currentPageIndex <= NUMBER_OF_PAGES; currentPageIndex++){
            List <Integer> itemsDataStartIndexes = new ArrayList<>();
            List <Integer> itemsDataEndIndexes = new ArrayList<>();
            getStartAndEndHeaderIndex(pageHtml, itemsDataStartIndexes, itemsDataEndIndexes);
            calculateItemFromPage(pageHtml, itemsDataStartIndexes, itemsDataEndIndexes);

            url = STATIC_URL_PART + currentPageIndex + SORTING;
            driver.get(url);
            waitForSiteLoad();
            pageHtml = driver.getPageSource();

        }
        driver.close();

    }

    public static void waitForSiteLoad() throws InterruptedException {
        new WebDriverWait(driver, 10).until(driver ->
                ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
    }

    private static void initWebDriver() {
        options = new FirefoxOptions();
        options.setHeadless(true);
        driver = new FirefoxDriver(options);
    }

    private static void calculateItemFromPage(String pageHtml, List<Integer> itemsDataStartIndexes, List<Integer> itemsDataEndIndexes) throws IOException, InterruptedException {
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
                if (couldNotCreateItem(currentItem)){
                    continue;
                }
                checkItemProfit(currentItem);


            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
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


    private static String cutStringBeginsDataHashName(int dataHashNameToSkipLen, String itemHeader) {
        itemHeader = itemHeader.substring(dataHashNameToSkipLen);
        return itemHeader;
    }

    private static String createItemHeader(String pageHtml, List<Integer> itemsDataStartIndexes, List<Integer> itemsDataEndIndexes, int itemIndex) {
        return pageHtml.substring(itemsDataStartIndexes.get(itemIndex), itemsDataEndIndexes.get(itemIndex));
    }

    private static void getStartAndEndHeaderIndex(String pageHtml, List<Integer> itemsDataStartIndexes, List<Integer> itemsDataEndIndexes) {
        for (int currentItemIndex = 0; currentItemIndex < ITEMS_ON_PAGE; currentItemIndex++){

            itemsDataStartIndexes.add(pageHtml.indexOf("\"result_" + currentItemIndex));
            try {
                String itemDataHtmlPart = pageHtml.substring(itemsDataStartIndexes.get(currentItemIndex));
                itemsDataEndIndexes.add(itemDataHtmlPart.indexOf(">") + itemsDataStartIndexes.get(currentItemIndex) + ONE_NEXT_INDEX);
            } catch (Exception e) {
                System.out.println("First error");
            }

        }
    }


}