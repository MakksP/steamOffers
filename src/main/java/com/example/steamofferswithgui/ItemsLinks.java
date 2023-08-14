package com.example.steamofferswithgui;

import java.util.HashMap;
import java.util.Map;

public class ItemsLinks {
    public static final String STAT_TRACK_KNIFE_STATIC_URL_PART = "https://steamcommunity.com/market/search?q=&category_730_ItemSet%5B%5D=any&category_730_ProPlayer%5B%5D=any&category_730_StickerCapsule%5B%5D=any&category_730_TournamentTeam%5B%5D=any&category_730_Weapon%5B%5D=any&category_730_Quality%5B%5D=tag_unusual_strange&appid=730#p";
    public static final String KNIFE_AND_GLOVES_STATIC_URL_PART = "https://steamcommunity.com/market/search?q=&category_730_ItemSet%5B%5D=any&category_730_ProPlayer%5B%5D=any&category_730_StickerCapsule%5B%5D=any&category_730_TournamentTeam%5B%5D=any&category_730_Weapon%5B%5D=any&category_730_Quality%5B%5D=tag_unusual&appid=730#p";
    public static final String GLOVES_STATIC_URL_PART = "https://steamcommunity.com/market/search?q=&category_730_ItemSet%5B%5D=any&category_730_ProPlayer%5B%5D=any&category_730_StickerCapsule%5B%5D=any&category_730_TournamentTeam%5B%5D=any&category_730_Weapon%5B%5D=any&category_730_Type%5B%5D=tag_Type_Hands&appid=730#p";
    private static Map<String, String> linksToItemsByName;

    public static void initItemsLinks(){
        linksToItemsByName = new HashMap<>();
        linksToItemsByName.put(ItemsComboBox.STAT_TRACK_KNIFE_NAME, STAT_TRACK_KNIFE_STATIC_URL_PART);
        linksToItemsByName.put(ItemsComboBox.KNIFE_AND_GLOVES_NAME, KNIFE_AND_GLOVES_STATIC_URL_PART);
        linksToItemsByName.put(ItemsComboBox.GLOVES_NAME, GLOVES_STATIC_URL_PART);
    }

    public static Map<String, String> getLinksToItemsByName(){
        return linksToItemsByName;
    }
}
