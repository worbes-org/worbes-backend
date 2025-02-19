package com.worbes.auctionhousetracker.utils;

import com.worbes.auctionhousetracker.entity.enums.Region;

import static com.worbes.auctionhousetracker.config.properties.RestClientConfigProperties.BASE_URL;

public class BlizzardApiUtils {

    public static String createUrl(Region region, String path) {
        return String.format(BASE_URL, region.getValue()).concat(path);
    }
}
