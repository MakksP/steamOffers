import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import org.apache.commons.text.StringEscapeUtils;

public class Item {
    private int dataAppid;
    private String dataHashName;
    public static final String STATIC_ITEM_LINK_PART = "https://steamcommunity.com/market/listings/";
    private String itemUrl;
    private StringBuilder pageHtml;
    private List<HistogramElement> itemSellHistogram;

    public Item(int dataAppid, String dataHashName){
        this.dataAppid = dataAppid;
        this.dataHashName = dataHashName;
        itemUrl = STATIC_ITEM_LINK_PART + dataAppid + "/" + StringEscapeUtils.unescapeHtml4(dataHashName);
    }

    public void makeConnectionToItem() throws IOException {
        HttpURLConnection pageConnection = MainSystem.connectToPage(itemUrl);

        BufferedReader reader = new BufferedReader(new InputStreamReader(pageConnection.getInputStream()));
        this.pageHtml = new StringBuilder();

        MainSystem.readDataFromPage(reader, pageHtml);
    }

    public void getHistogramFromPage(){

    }
}
