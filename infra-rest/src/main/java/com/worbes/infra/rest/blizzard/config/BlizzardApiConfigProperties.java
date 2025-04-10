package com.worbes.infra.rest.blizzard.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "blizzard.api")
@Getter
@Setter
public class BlizzardApiConfigProperties {
    private String tokenUrl;
    private String tokenKey;
    private String id;
    private String secret;
}
