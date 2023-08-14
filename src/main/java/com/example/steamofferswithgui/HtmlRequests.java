package com.example.steamofferswithgui;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HtmlRequests {
    public static final String PAGE_HEADER = "market_paging_pagelink";
    public static FirefoxOptions pageOptions;
    public static WebDriver pageDriver;
    public static final int MAX_NEXT_PAGE_WAIT_TIME = 10;
    public static final int MAX_BUY_ORDER_WAIT_TIME = 10;
    public static final String BUY_ORDER_HEADER = "market_commodity_orders_header_promote";

    public static int waitForSiteLoad() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(pageDriver, MAX_NEXT_PAGE_WAIT_TIME);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className(PAGE_HEADER)));
            return 0;

        } catch (Exception e){
            System.out.println("Failed to get pages");
            pageDriver.navigate().refresh();
            return -1;
        }
    }

    public static void initPageWebDriver() {
        HtmlRequests.pageOptions = new FirefoxOptions();
        HtmlRequests.pageOptions.setHeadless(true);
        HtmlRequests.pageDriver = new FirefoxDriver(HtmlRequests.pageOptions);
    }

    public static String getHtml(String url) throws InterruptedException {
        pageDriver.get(url);
        waitForSiteLoad();
        waitForNextPageLoad();
        String pageHtml = pageDriver.getPageSource();
        return pageHtml;
    }

    public static boolean couldNotLoadItemsFromNextPage() {
        return waitForNextPageLoad() == -1;
    }

    public static int waitForNextPageLoad() {
        WebDriverWait wait = new WebDriverWait(HtmlRequests.pageDriver, MAX_NEXT_PAGE_WAIT_TIME);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("result_0")));
            return 0;

        } catch (Exception e){
            System.out.println("Failed to load next page");
            return -1;
        }
    }

    public static String getHtmlFromNextPage(String url) throws InterruptedException {
        pageDriver.get(url);
        if (couldNotLoadItemsFromNextPage()){
            pageDriver.navigate().refresh();
            return null;
        }
        String pageHtml = pageDriver.getPageSource();
        return pageHtml;
    }

    public static boolean couldNotGetPages() {
        return MainSystem.NUMBER_OF_PAGES == -1;
    }

    public static int waitForBuyOrdersLoad() {
        WebDriverWait wait = new WebDriverWait( HtmlRequests.pageDriver, MAX_BUY_ORDER_WAIT_TIME);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className(BUY_ORDER_HEADER)));
            return 0;
        } catch (Exception e){
            System.out.println("Failed to load buy orders");
            return -1;
        }
    }
}
