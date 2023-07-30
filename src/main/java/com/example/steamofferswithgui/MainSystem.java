package com.example.steamofferswithgui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainSystem {

    public static final int NUMBER_OF_PAGES = 44268;
    public static final String SORTING = "&count=10&search_descriptions=0&sort_column=popular&sort_dir=desc&appid=730&category_730_ItemSet%5B%5D=any&category_730_ProPlayer%5B%5D=any&category_730_StickerCapsule%5B%5D=any&category_730_TournamentTeam%5B%5D=any&category_730_Weapon%5B%5D=any&category_730_Quality%5B%5D=tag_unusual_strange";
    public static final String STATIC_URL_PART = "https://steamcommunity.com/market/search/render/?query=&start=";
    public static final int ITEMS_ON_PAGE = 10;
    public static final int ONE_NEXT_INDEX = 1;
    public static final int BEGIN_INDEX = 0;
    public static final int EXTRA_CHARS = 3;

    public static void startSteamOffersSystem() throws IOException, InterruptedException {

        for (int currentPageIndex = 0; currentPageIndex <= NUMBER_OF_PAGES; currentPageIndex+=10){
            String url = STATIC_URL_PART + currentPageIndex + SORTING;

            HttpURLConnection pageConnection = connectToPage(url);
            Thread.sleep(Item.GET_PAUSE_TIME_MILLS);
            BufferedReader reader = new BufferedReader(new InputStreamReader(pageConnection.getInputStream()));
            StringBuilder pageHtml = new StringBuilder();
            readDataFromPage(reader, pageHtml);

            List <Integer> itemsDataStartIndexes = new ArrayList<>();
            List <Integer> itemsDataEndIndexes = new ArrayList<>();
            getStartAndEndHeaderIndex(pageHtml, itemsDataStartIndexes, itemsDataEndIndexes);

            List<Item> itemsOnPage = CreateItemsFromPage(pageHtml, itemsDataStartIndexes, itemsDataEndIndexes);

            for (Item currentItem : itemsOnPage){
                int points = currentItem.analyseItem();
                if (points > 0){
                    System.out.println(currentItem.getDataHashName() + ", punkty opłacalnośći: " + points);
                }
            }

            reader.close();
        }

    }

    private static List<Item> CreateItemsFromPage(StringBuilder pageHtml, List<Integer> itemsDataStartIndexes, List<Integer> itemsDataEndIndexes) throws IOException, InterruptedException {
        List<Item> itemsOnPage = new ArrayList<>();
        int partToSkipLen = "\"result_0\\\" data-appid=\\\"".length();
        int dataHashNameToSkipLen = "\\\" data-hash-name=\\\"".length() + EXTRA_CHARS;
        for (int itemIndex = 0; itemIndex < ITEMS_ON_PAGE; itemIndex++) {
            String itemHeader = createItemHeader(pageHtml, itemsDataStartIndexes, itemsDataEndIndexes, partToSkipLen, itemIndex);
            int endDataAppidIndex = itemHeader.indexOf("\\");
            int dataAppid = Integer.parseInt(itemHeader.substring(BEGIN_INDEX, endDataAppidIndex));

            itemHeader = cutStringBeginsDataHashName(dataHashNameToSkipLen, itemHeader);
            int endDataHashNameIndex = itemHeader.indexOf("\\");
            String dataHashName = itemHeader.substring(BEGIN_INDEX, endDataHashNameIndex);
            itemsOnPage.add(new Item(dataAppid, dataHashName));
        }
        return itemsOnPage;
    }

    private static String cutStringBeginsDataHashName(int dataHashNameToSkipLen, String itemHeader) {
        itemHeader = itemHeader.substring(dataHashNameToSkipLen);
        return itemHeader;
    }

    private static String createItemHeader(StringBuilder pageHtml, List<Integer> itemsDataStartIndexes, List<Integer> itemsDataEndIndexes, int partToSkipLen, int itemIndex) {
        return pageHtml.substring(itemsDataStartIndexes.get(itemIndex) + partToSkipLen, itemsDataEndIndexes.get(itemIndex));
    }

    private static void getStartAndEndHeaderIndex(StringBuilder pageHtml, List<Integer> itemsDataStartIndexes, List<Integer> itemsDataEndIndexes) {
        for (int currentItemIndex = 0; currentItemIndex < ITEMS_ON_PAGE; currentItemIndex++){

            itemsDataStartIndexes.add(pageHtml.indexOf("\"result_" + currentItemIndex));
            String itemDataHtmlPart = pageHtml.substring(itemsDataStartIndexes.get(currentItemIndex));
            itemsDataEndIndexes.add(itemDataHtmlPart.indexOf(">") + itemsDataStartIndexes.get(currentItemIndex) + ONE_NEXT_INDEX);
        }
    }

    public static void readDataFromPage(BufferedReader reader, StringBuilder pageHtml) throws IOException {
        String singleHtmlLine;
        while ((singleHtmlLine = reader.readLine()) != null){
            pageHtml.append(singleHtmlLine);
        }
    }

    public static HttpURLConnection connectToPage(String url) throws IOException {
        URL currentItemUrl = new URL(url);
        HttpURLConnection pageConnection = (HttpURLConnection) currentItemUrl.openConnection();
        try {
            pageConnection.setRequestMethod("GET");
        } catch (Error e){
            System.out.println("Odrzucono połączenie");
        }
        return pageConnection;
    }
}