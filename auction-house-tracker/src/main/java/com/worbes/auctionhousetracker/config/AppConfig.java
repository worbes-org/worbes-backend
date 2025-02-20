package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.config.properties.BlizzardApiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    BlizzardApiConfigProperties blizzardApiConfigProperties() {
        return new BlizzardApiConfigProperties();
    }
}
