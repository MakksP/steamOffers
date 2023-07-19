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

    public Item(int dataAppid, String dataHashName) throws IOException {
        this.dataAppid = dataAppid;
        this.dataHashName = dataHashName;
        itemUrl = STATIC_ITEM_LINK_PART + dataAppid + "/" + StringEscapeUtils.unescapeHtml4(dataHashName).replaceAll(" ", "%20");
        itemSellHistogram = new ArrayList<>();
        this.makeConnectionToItem();
        this.createHistogramFromPage();
        this.findMostExpensiveBuyOrder();
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

    public void findMostExpensiveBuyOrder() throws IOException {
        String itemNameId = Format.getItemNameIdFromHtmlPage(pageHtml);
        HttpURLConnection pageConnection = MainSystem.connectToPage(STATIC_ORDER_GET_FIRST_PART + itemNameId + STATIC_ORDER_GET_SECOND_PART);
        BufferedReader reader = new BufferedReader(new InputStreamReader(pageConnection.getInputStream()));
        orderBuyPageHtml = new StringBuilder();

        MainSystem.readDataFromPage(reader, orderBuyPageHtml);
        mostExpensiveBuyOrder = Format.cutMostExpensiveOrderFromHtml(orderBuyPageHtml);
        reader.close();
        System.out.println(mostExpensiveBuyOrder);
    }


    public String getMostExpensiveBuyOrder() {
        return mostExpensiveBuyOrder;
    }

}
