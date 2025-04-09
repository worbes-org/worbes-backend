package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.config.properties.BlizzardApiConfigProperties;
import com.worbes.auctionhousetracker.config.properties.RequiredItemClassesProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@EnableConfigurationProperties({
        BlizzardApiConfigProperties.class,
        RequiredItemClassesProperties.class})
public class AppConfig {

}
