package com.example.steamofferswithgui;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.text.StringEscapeUtils;

import static com.example.steamofferswithgui.MainSystem.driver;

public class Item {
    public static final String PRICES_HISTOGRAM_HEADER = "$J(document).ready(function(){";
    public static final int START_INDEX = 0;
    public static final int MIN_SELLS_IN_MONTH = 8;
    public static final int VALUE_OF_SOLD_COUNT = 5;
    public static final int ONE_HUNDRED_PERCENT = 1;
    public static final double TAX_PART = 0.15;
    public static final double BONUS_PER_VALUE = 2.5;
    public static final int WAIT_FOR_SITE_LOAD_TIME = 2000;
    private int dataAppid;
    private String dataHashName;
    public static final String STATIC_ITEM_LINK_PART = "https://steamcommunity.com/market/listings/";
    private String itemUrl;
    private String pageHtml;
    private List<HistogramElement> itemSellHistogram;
    private String mostExpensiveBuyOrder;

    public Item(int dataAppid, String dataHashName) throws IOException, InterruptedException {
        this.dataAppid = dataAppid;
        this.dataHashName = dataHashName;
        itemUrl = STATIC_ITEM_LINK_PART + dataAppid + "/" + StringEscapeUtils.unescapeHtml4(dataHashName).replaceAll(" ", "%20");
        itemSellHistogram = new ArrayList<>();
        this.makeConnectionToItem();
        this.createHistogramFromPage();
        System.out.println("Zaczytał: " + this.dataHashName);
        this.findMostExpensiveBuyOrder();
        if (mostExpensiveBuyOrder == null){
            System.out.println("ERROR - SKIP");
        }
        System.out.println("Znalazł najdroższe zlecenie");
    }

    public void makeConnectionToItem() throws IOException {

        driver.get(itemUrl);
        try {
            Thread.sleep(WAIT_FOR_SITE_LOAD_TIME);
            pageHtml = driver.getPageSource();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void createHistogramFromPage(){
        String histogramFromHtml = getHistogramFromHtmlPage();
        LocalDate currentDate = LocalDate.now();
        List<String> histogramSells = new ArrayList<>(List.of(histogramFromHtml.split("]")));
        Format.deleteTwoFirstExtraCharsFromHistogram(histogramSells);
        for (int currentSoldItemDateIndex = histogramSells.size() - 1; currentSoldItemDateIndex >= 0; currentSoldItemDateIndex--) {
            String currentSoldItem = histogramSells.get(currentSoldItemDateIndex);
            List<String> tempDate = Format.divideHistogramElementData(currentSoldItem);
            List<String> priceAndCount = Format.formatCountAndPrice(tempDate);
            Format.updateCountPriceToFormatted(tempDate, priceAndCount);
            HistogramElement histogramElement = Format.createHistogramElement(tempDate);
            if (histogramElement.isWithinDateRange(currentDate)){
                itemSellHistogram.add(histogramElement);
            } else {
                break;
            }
        }

    }


    private String getHistogramFromHtmlPage() {
        String histogramFromHtml = pageHtml.substring(pageHtml.indexOf(PRICES_HISTOGRAM_HEADER));
        histogramFromHtml = histogramFromHtml.substring(histogramFromHtml.indexOf("["));
        histogramFromHtml = histogramFromHtml.substring(START_INDEX,  histogramFromHtml.indexOf(";"));
        return histogramFromHtml;
    }

    public void findMostExpensiveBuyOrder() throws IOException, InterruptedException {
        String itemNameId = Format.getItemNameIdFromHtmlPage(pageHtml);
        mostExpensiveBuyOrder = Format.cutMostExpensiveOrderFromHtml(pageHtml);
    }

    public int analyseItem(){
        int profitPoints = 0;
        if (this.itemSellHistogram.size() < MIN_SELLS_IN_MONTH){
            return profitPoints;
        }
        for (HistogramElement histogramElement : itemSellHistogram){
            if (Double.parseDouble(mostExpensiveBuyOrder) * (ONE_HUNDRED_PERCENT + TAX_PART) < histogramElement.price){
                profitPoints += (VALUE_OF_SOLD_COUNT * histogramElement.count) + histogramElement.count * (histogramElement.price / BONUS_PER_VALUE);
            }
        }

        return profitPoints;
    }

    public String getDataHashName() {
        return dataHashName;
    }

    public String getMostExpensiveBuyOrder(){
        return mostExpensiveBuyOrder;
    }
}
