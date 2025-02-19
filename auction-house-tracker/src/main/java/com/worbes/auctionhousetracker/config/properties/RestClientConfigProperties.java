package com.worbes.auctionhousetracker.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blizzard.api")
@Getter
@Setter
public class RestClientConfigProperties {
    public static final String BASE_URL = "https://%s.api.blizzard.com";

    //paths
    public static final String COMMODITIES_PATH = "/data/wow/auctions/commodities";
    public static final String ITEM_CLASS_PATH = "/data/wow/item-class/%s";
    public static final String ITEM_SUBCLASS_PATH = "/data/wow/item-class/%s/item-subclass/%s";
    public static final String ITEM_CLASS_INDEX_PATH = "/data/wow/item-class/index";

    //params
    public static final String NAMESPACE_KEY = "namespace";
    public static final String NAMESPACE_DYNAMIC = "dynamic-%s";
    public static final String NAMESPACE_STATIC = "dynamic-%s";

    private String baseUrl;
    private String baseUrlKr;
    private String baseUrlUs;
    private String itemClassIndexUrl;
    private String itemClassUrl;
    private String itemSubclassUrl;
    private String commoditiesUrl;
}
