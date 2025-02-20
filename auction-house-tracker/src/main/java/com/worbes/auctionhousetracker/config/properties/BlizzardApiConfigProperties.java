package com.worbes.auctionhousetracker.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blizzard.api")
@Getter
@Setter
public class BlizzardApiConfigProperties {
    private String tokenUrl;
    private String id;
    private String secret;
}
