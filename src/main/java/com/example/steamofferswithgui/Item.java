package com.example.steamofferswithgui;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.openqa.selenium.JavascriptExecutor;


public class Item {
    public static final String PRICES_HISTOGRAM_HEADER = "$J(document).ready(function(){";
    public static final int START_INDEX = 0;
    public static final int MIN_SELLS_IN_MONTH = 8;
    public static final int ONE_HUNDRED_PERCENT = 1;
    public static final double TAX_PART = 0.15;
    public static final int NEXT_ITEM_LOAD_TIME_WAIT = 5000;
    public static final double MIN_PROFIT_USD = 7.5;
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
        if (failedToMakeConnectionWithItem()){
            this.dataHashName = "INVALID";
            return;
        }
        this.createHistogramFromPage();
        System.out.println("Zaczytał: " + this.dataHashName);
        this.findMostExpensiveBuyOrder();
        if (mostExpensiveBuyOrder == null){
            System.out.println("ERROR - SKIP");
        }
        System.out.println("Znalazł najdroższe zlecenie");
    }

    private boolean failedToMakeConnectionWithItem() throws IOException, InterruptedException {
        Thread.sleep(NEXT_ITEM_LOAD_TIME_WAIT);
        return this.makeConnectionToItem() == -1;
    }

    public int makeConnectionToItem() throws IOException, InterruptedException {

        HtmlRequests.pageDriver.get(itemUrl);
        if (waitingForBuyOrdersTimedOut()){
            return -1;
        }
        System.out.println(((JavascriptExecutor)  HtmlRequests.pageDriver).executeScript("return document.readyState"));
        pageHtml =  HtmlRequests.pageDriver.getPageSource();
        return 0;
    }

    private static boolean waitingForBuyOrdersTimedOut() {
        return HtmlRequests.waitForBuyOrdersLoad() == -1;
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
        mostExpensiveBuyOrder = Format.cutMostExpensiveOrderFromHtml(pageHtml);
    }

    public int analyseItem(){
        int profitPoints = 0;
        if (this.itemSellHistogram.size() < MIN_SELLS_IN_MONTH){
            return profitPoints;
        }
        double minSellPrice = Double.parseDouble(mostExpensiveBuyOrder) * (ONE_HUNDRED_PERCENT + TAX_PART);
        List<HistogramElement> elementsToRemove = new ArrayList<>();
        getElementsToRemove(minSellPrice, elementsToRemove);
        removeNonBeneficialPoints(elementsToRemove);
        double itemsPriceMedian = calculateMedian();
        return calculatePoints(profitPoints, itemsPriceMedian);
    }

    private int calculatePoints(int profitPoints, double itemsPriceMedian) {
        for (HistogramElement histogramElement : itemSellHistogram){
            if (itemsPriceMedian * 1.2 >= histogramElement.price){
                profitPoints += histogramElement.price * histogramElement.count;
            }
        }
        return profitPoints;
    }

    private void removeNonBeneficialPoints(List<HistogramElement> elementsToRemove) {
        for (int elementsToRemoveIndex = 0; elementsToRemoveIndex < elementsToRemove.size(); elementsToRemoveIndex++) {
            itemSellHistogram.remove(elementsToRemove.get(elementsToRemoveIndex));
        }
    }

    private double calculateMedian(){
        itemSellHistogram.sort(Comparator.comparingDouble(HistogramElement::getPrice));
        if (itemSellHistogram.size() % 2 == 0){
            return itemSellHistogram.get(itemSellHistogram.size() / 2).getPrice() + itemSellHistogram.get(itemSellHistogram.size() / 2 + 1).getPrice() / 2;
        }
        return itemSellHistogram.get(itemSellHistogram.size() / 2 + 1).getPrice();
    }

    private void getElementsToRemove(double minSellPrice, List<HistogramElement> elementsToRemove) {
        for (HistogramElement histogramElement : itemSellHistogram){
            if (minSellPrice + MIN_PROFIT_USD > histogramElement.price){
                elementsToRemove.add(histogramElement);
            }
        }
    }

    public String getDataHashName() {
        return dataHashName;
    }

    public String getMostExpensiveBuyOrder(){
        return mostExpensiveBuyOrder;
    }
}
