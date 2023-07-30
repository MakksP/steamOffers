import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.text.StringEscapeUtils;

public class Item {
    public static final String PRICES_HISTOGRAM_HEADER = "$J(document).ready(function(){";
    public static final int START_INDEX = 0;
    public static final int MIN_SELLS_IN_MONTH = 8;
    public static final int VALUE_OF_SOLD_COUNT = 5;
    public static final int ONE_HUNDRED_PERCENT = 1;
    public static final double TAX_PART = 0.15;
    public static final double BONUS_PER_VALUE = 2.5;
    public static final int GET_PAUSE_TIME_MILLS = 4000;
    private int dataAppid;
    private String dataHashName;
    public static final String STATIC_ITEM_LINK_PART = "https://steamcommunity.com/market/listings/";
    public static final String STATIC_ORDER_GET_FIRST_PART = "https://steamcommunity.com/market/itemordershistogram?country=PL&language=polish&currency=1&item_nameid=";
    public static final String STATIC_ORDER_GET_SECOND_PART = "&two_factor=0";
    private String itemUrl;
    private StringBuilder pageHtml;
    private StringBuilder orderBuyPageHtml;
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
        Thread.sleep(GET_PAUSE_TIME_MILLS);
        this.findMostExpensiveBuyOrder();
        System.out.println("Znalazł najdroższe zlecenie");
        Thread.sleep(GET_PAUSE_TIME_MILLS);
    }

    public void makeConnectionToItem() throws IOException {
        HttpURLConnection pageConnection = MainSystem.connectToPage(itemUrl);

        BufferedReader reader = new BufferedReader(new InputStreamReader(pageConnection.getInputStream()));
        this.pageHtml = new StringBuilder();

        MainSystem.readDataFromPage(reader, pageHtml);
        reader.close();

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
        HttpURLConnection pageConnection = MainSystem.connectToPage(STATIC_ORDER_GET_FIRST_PART + itemNameId + STATIC_ORDER_GET_SECOND_PART);
        Thread.sleep(GET_PAUSE_TIME_MILLS);
        BufferedReader reader = new BufferedReader(new InputStreamReader(pageConnection.getInputStream()));
        orderBuyPageHtml = new StringBuilder();

        MainSystem.readDataFromPage(reader, orderBuyPageHtml);
        mostExpensiveBuyOrder = Format.cutMostExpensiveOrderFromHtml(orderBuyPageHtml);
        reader.close();
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
}
