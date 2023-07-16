public class Item {
    private int dataAppid;
    private String dataHashName;
    public static final String STATIC_ITEM_LINK_PART = "https://steamcommunity.com/market/listings/";
    private String itemUrl;

    public Item(int dataAppid, String dataHashName){
        this.dataAppid = dataAppid;
        this.dataHashName = dataHashName;
        itemUrl = STATIC_ITEM_LINK_PART + dataAppid + "/" + dataHashName;
    }
}
