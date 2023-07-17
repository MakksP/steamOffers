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
    private String itemUrl;
    private StringBuilder pageHtml;
    private List<HistogramElement> itemSellHistogram;

    public Item(int dataAppid, String dataHashName){
        this.dataAppid = dataAppid;
        this.dataHashName = dataHashName;
        itemUrl = STATIC_ITEM_LINK_PART + dataAppid + "/" + StringEscapeUtils.unescapeHtml4(dataHashName).replaceAll(" ", "%20");
    }

    public void makeConnectionToItem() throws IOException {
        HttpURLConnection pageConnection = MainSystem.connectToPage(itemUrl);

        BufferedReader reader = new BufferedReader(new InputStreamReader(pageConnection.getInputStream()));
        this.pageHtml = new StringBuilder();

        MainSystem.readDataFromPage(reader, pageHtml);

    }

    public void getHistogramFromPage(){
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
            System.out.println(tempDate);
        }

    }


    private String getHistogramFromHtmlPage() {
        String histogramFromHtml = pageHtml.substring(pageHtml.indexOf(PRICES_HISTOGRAM_HEADER));
        histogramFromHtml = histogramFromHtml.substring(histogramFromHtml.indexOf("["));
        histogramFromHtml = histogramFromHtml.substring(START_INDEX,  histogramFromHtml.indexOf(";"));
        return histogramFromHtml;
    }
}
