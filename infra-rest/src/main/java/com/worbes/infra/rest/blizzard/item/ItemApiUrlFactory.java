package com.worbes.infra.rest.blizzard.item;

import com.worbes.domain.shared.RegionType;
import com.worbes.infra.rest.blizzard.support.BlizzardApiUrlBuilder;

public class ItemApiUrlFactory {

    public static String itemClassesIndexUrl(RegionType region) {
        return BlizzardApiUrlBuilder.builder()
                .region(region)
                .path("/data/wow/item-class/index")
                .build();
    }

    public static String itemClassUrl(RegionType region, Long itemId) {
        return BlizzardApiUrlBuilder.builder()
                .region(region)
                .path(String.format("/data/wow/item-class/%s", itemId))
                .build();
    }

    public static String itemSubclassUrl(RegionType region, Long itemClassId, Long subclassId) {
        return BlizzardApiUrlBuilder.builder()
                .region(region)
                .path(String.format("/data/wow/item-class/%s/item-subclass/%s", itemClassId, subclassId))
                .build();
    }

    public static String itemUrl(RegionType region, Long itemId) {
        return BlizzardApiUrlBuilder.builder()
                .region(region)
                .path(String.format("/data/wow/item/%s", itemId))
                .build();
    }

    public static String mediaUrl(RegionType region, Long itemId) {
        return BlizzardApiUrlBuilder.builder()
                .region(region)
                .path(String.format("/data/wow/media/item/%s", itemId))
                .build();
    }
}
