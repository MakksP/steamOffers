package com.example.steamofferswithgui;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class Format {

    public static final int PROPER_DATA_START_INDEX = 2;
    public static final int LETTER_OPPOSITE_QUATION_MARK = 1;
    public static final int MONTH_INDEX = 0;
    public static final int DAY_INDEX = 1;
    public static final int YEAR_INDEX = 2;
    public static final int PRICE_AND_COUNT_INDEX = 4;
    public static final int INDEX_AFTER_HYPHEN = 1;
    public static final int PRICE_INDEX = 4;
    public static final int COUNT_INDEX = 5;
    public static final String ITEM_NAME_ID_HEADER = "Market_LoadOrderSpread(";
    public static final int SECOND_SPACE_POSITION = 1;
    public static final int FIRST_SPACE_POSITION = 1;
    public static final String MOST_EXPENSIVE_ORDER_HEADER = "requests to buy at";
    public static final int SKIP_TWO_CHARS = 2;
    public static final int BEGIN_INDEX = 0;

    public static int getMonthNumber(String month){
        if (month.equals("Jan")){
            return 1;
        }else if (month.equals("Feb")){
            return 2;
        } else if (month.equals("Mar")){
            return 3;
        } else if (month.equals("Apr")){
            return 4;
        } else if (month.equals("May")){
            return 5;
        } else if (month.equals("Jun")){
            return 6;
        } else if (month.equals("Jul")){
            return 7;
        } else if (month.equals("Aug")){
            return 8;
        } else if (month.equals("Sep")){
            return 9;
        } else if (month.equals("Oct")){
            return 10;
        } else if (month.equals("Nov")){
            return 1;
        } else if (month.equals("Dec")){
            return 12;
        }
        return -1;
    }

    public static HistogramElement createHistogramElement(List<String> tempDate) {
        return new HistogramElement(LocalDate.of(Integer.parseInt(tempDate.get(YEAR_INDEX)), Month.of(Format.getMonthNumber(tempDate.get(MONTH_INDEX))),
                Integer.parseInt(tempDate.get(DAY_INDEX))),
                Double.parseDouble(tempDate.get(PRICE_INDEX)), Integer.parseInt(tempDate.get(COUNT_INDEX)));
    }

    public static List<String> divideHistogramElementData(String currentSoldItem) {
        List <String> tempDate = new ArrayList<>(List.of(currentSoldItem.split(" ")));
        tempDate.set(MONTH_INDEX, tempDate.get(MONTH_INDEX).substring(LETTER_OPPOSITE_QUATION_MARK));
        String currentItemPriceAndCount = tempDate.get(PRICE_AND_COUNT_INDEX);
        tempDate.set(PRICE_AND_COUNT_INDEX, currentItemPriceAndCount.substring(currentItemPriceAndCount.indexOf(",") + INDEX_AFTER_HYPHEN));
        return tempDate;
    }

    public static void updateCountPriceToFormatted(List<String> tempDate, List<String> priceAndCount) {
        tempDate.set(PRICE_INDEX, priceAndCount.get(0));
        tempDate.add(COUNT_INDEX, priceAndCount.get(1));
    }

    public static List<String> formatCountAndPrice(List<String> tempDate) {
        List<String> priceAndCount = new ArrayList<>(List.of(tempDate.get(PRICE_AND_COUNT_INDEX).split(",")));
        priceAndCount.set(0, priceAndCount.get(0).replace("\"", ""));
        priceAndCount.set(1, priceAndCount.get(1).replace("\"", ""));
        return priceAndCount;
    }

    public static void deleteTwoFirstExtraCharsFromHistogram(List<String> histogramSells) {
        for (int histogramSellIndex = 0; histogramSellIndex < histogramSells.size(); histogramSellIndex++){
            String cutFirstTwoCharsSell = histogramSells.get(histogramSellIndex).substring(PROPER_DATA_START_INDEX);
            histogramSells.set(histogramSellIndex, cutFirstTwoCharsSell);
        }
    }

    public static String getItemNameIdFromHtmlPage(String pageHtml) {
        String itemNameId = pageHtml.substring(pageHtml.indexOf(ITEM_NAME_ID_HEADER));
        itemNameId = itemNameId.substring(itemNameId.indexOf(" ") + FIRST_SPACE_POSITION, itemNameId.indexOf(")") - SECOND_SPACE_POSITION);
        return itemNameId;
    }

    public static String cutMostExpensiveOrderFromHtml(String orderBuyPageHtml) {
        String mostExpensiveBuyOrder = orderBuyPageHtml.substring(orderBuyPageHtml.indexOf(MOST_EXPENSIVE_ORDER_HEADER));
        mostExpensiveBuyOrder = mostExpensiveBuyOrder.substring(mostExpensiveBuyOrder.indexOf(">") + SKIP_TWO_CHARS);
        mostExpensiveBuyOrder = mostExpensiveBuyOrder.substring(BEGIN_INDEX, mostExpensiveBuyOrder.indexOf("<"));
        return mostExpensiveBuyOrder;
    }
}
