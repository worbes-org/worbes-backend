package com.worbes.auctionhousetracker.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("blizzard.oauth2")
@Getter
@Setter
public class OAuth2ConfigProperties {
    private String tokenUrl;
    private String id;
    private String secret;
}
