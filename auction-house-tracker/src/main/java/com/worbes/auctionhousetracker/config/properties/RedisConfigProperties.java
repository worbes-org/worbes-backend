package com.worbes.auctionhousetracker.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
@Getter
@Setter
public class RedisConfigProperties {
    private String host;
    private int port;
}
