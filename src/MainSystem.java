
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainSystem {

    public static final int NUMBER_OF_PAGES = 44268;
    public static final String SORTING = "&count=10&search_descriptions=0&sort_column=popular&sort_dir=desc";
    public static final String STATIC_URL_PART = "https://steamcommunity.com/market/search/render/?query=&start=";
    public static final int ITEMS_ON_PAGE = 10;
    public static final int ONE_NEXT_INDEX = 1;
    public static void main(String[] args) throws IOException {

        for (int currentPageIndex = 0; currentPageIndex <= NUMBER_OF_PAGES; currentPageIndex+=10){
            String url = STATIC_URL_PART + currentPageIndex + SORTING;

            HttpURLConnection pageConnection = connectToPage(url);

            BufferedReader reader = new BufferedReader(new InputStreamReader(pageConnection.getInputStream()));
            StringBuilder pageHtml = new StringBuilder();
            readDataFromPage(reader, pageHtml);

            List <Integer> itemsDataStartIndexes = new ArrayList<>();
            List <Integer> itemsDataEndIndexes = new ArrayList<>();
            getStartAndEndHeaderIndex(pageHtml, itemsDataStartIndexes, itemsDataEndIndexes);

            displayPageHeaders(pageHtml, itemsDataStartIndexes, itemsDataEndIndexes);
            reader.close();
        }

    }

    private static void displayPageHeaders(StringBuilder pageHtml, List<Integer> itemsDataStartIndexes, List<Integer> itemsDataEndIndexes) {
        for (int i = 0; i < 10; i++) {
            System.out.println(pageHtml.substring(itemsDataStartIndexes.get(i), itemsDataEndIndexes.get(i)));
        }
    }

    private static void getStartAndEndHeaderIndex(StringBuilder pageHtml, List<Integer> itemsDataStartIndexes, List<Integer> itemsDataEndIndexes) {
        for (int currentItemIndex = 0; currentItemIndex < ITEMS_ON_PAGE; currentItemIndex++){

            itemsDataStartIndexes.add(pageHtml.indexOf("\"result_" + currentItemIndex));
            String itemDataHtmlPart = pageHtml.substring(itemsDataStartIndexes.get(currentItemIndex));
            itemsDataEndIndexes.add(itemDataHtmlPart.indexOf(">") + itemsDataStartIndexes.get(currentItemIndex) + ONE_NEXT_INDEX);
        }
    }

    private static void readDataFromPage(BufferedReader reader, StringBuilder pageHtml) throws IOException {
        String singleHtmlLine;
        while ((singleHtmlLine = reader.readLine()) != null){
            pageHtml.append(singleHtmlLine);
        }
    }

    private static HttpURLConnection connectToPage(String url) throws IOException {
        URL currentItemUrl = new URL(url);
        HttpURLConnection pageConnection = (HttpURLConnection) currentItemUrl.openConnection();
        pageConnection.setRequestMethod("GET");
        if (pageConnection.getResponseCode() != 200){
            System.out.println("Odrzucono połączenie");
        } else {
            System.out.println("Połączono");
        }
        return pageConnection;
    }
}