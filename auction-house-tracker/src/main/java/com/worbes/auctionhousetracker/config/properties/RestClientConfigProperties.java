package com.worbes.auctionhousetracker.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blizzard.api")
@Getter
@Setter
public class RestClientConfigProperties {
    private String encoding;
    //base url;
    private String baseUrlKr;
    private String baseUrlUs;

    //path
    private String itemClassIndexUrl;
    private String itemClassUrl;
    private String itemSubclassUrl;
}
