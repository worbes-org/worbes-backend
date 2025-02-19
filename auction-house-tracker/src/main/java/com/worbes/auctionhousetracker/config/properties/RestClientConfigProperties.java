package com.worbes.auctionhousetracker.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blizzard.api")
@Getter
@Setter
public class RestClientConfigProperties {
    public static final String BASE_URL = "https://%s.api.blizzard.com";
    public static final String COMMODITIES_URL = "/data/wow/auctions/commodities";
    public static final String NAMESPACE_KEY = "namespace";
    public static final String NAMESPACE_DYNAMIC = "dynamic-%s";
    public static final String NAMESPACE_STATIC = "dynamic-%s";
    //base url;
    private String baseUrl;
    private String baseUrlKr;
    private String baseUrlUs;
    //path
    private String itemClassIndexUrl;
    private String itemClassUrl;
    private String itemSubclassUrl;
    private String commoditiesUrl;
}
