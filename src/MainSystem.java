
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainSystem {

    public static final int NUMBER_OF_PAGES = 44215;
    public static final String SORTING = "&count=10&search_descriptions=0&sort_column=popular&sort_dir=desc";
    public static final int ITEMS_ON_PAGE = 10;
    public static final int ONE_NEXT_INDEX = 1;
    //todo nie da sie robic tego tak na linkach tezeba symulować klikanie przycisku
    public static void main(String[] args) throws IOException {

        String staticUrlPart = "https://steamcommunity.com/market/search/render/?query=&start=";

        for (int currentPageIndex = 0; currentPageIndex <= NUMBER_OF_PAGES; currentPageIndex+=10){
            String url = staticUrlPart + currentPageIndex + SORTING;

            URL currentItemUrl = new URL(url);
            HttpURLConnection pageConnection = (HttpURLConnection) currentItemUrl.openConnection();
            pageConnection.setRequestMethod("GET");
            if (pageConnection.getResponseCode() != 200){
                System.out.println("Odrzucono połączenie");
            } else {
                System.out.println("Połączono");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(pageConnection.getInputStream()));
            StringBuilder pageHtml = new StringBuilder();
            String singleHtmlLine;
            while ((singleHtmlLine = reader.readLine()) != null){
                pageHtml.append(singleHtmlLine);
            }
            List <Integer> itemsDataStartIndexes = new ArrayList<>();
            List <Integer> itemsDataEndIndexes = new ArrayList<>();
            for (int currentItemIndex = 0; currentItemIndex < ITEMS_ON_PAGE; currentItemIndex++){

                itemsDataStartIndexes.add(pageHtml.indexOf("\"result_" + currentItemIndex));
                String itemDataHtmlPart = pageHtml.substring(itemsDataStartIndexes.get(currentItemIndex));
                itemsDataEndIndexes.add(itemDataHtmlPart.indexOf(">") + itemsDataStartIndexes.get(currentItemIndex) + ONE_NEXT_INDEX);
            }
            for (int i = 0; i < 10; i++) {
                System.out.println(pageHtml.substring(itemsDataStartIndexes.get(i), itemsDataEndIndexes.get(i)));
            }
            reader.close();
        }

    }
}