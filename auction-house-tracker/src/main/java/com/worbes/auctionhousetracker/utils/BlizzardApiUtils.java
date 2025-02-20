package com.worbes.auctionhousetracker.utils;

import com.worbes.auctionhousetracker.entity.enums.Region;

public class BlizzardApiUtils {

    public static String createUrl(Region region, String path) {
        return String.format("https://%s.api.blizzard.com", region.getValue()).concat(path);
    }
}
