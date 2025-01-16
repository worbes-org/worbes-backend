package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.service.ItemClassService;
import com.worbes.auctionhousetracker.service.ItemSubclassService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class DataConfig {

    @Bean
    @Order(1)
    public CommandLineRunner itemClassDataLoader(ItemClassService itemClassService, BearerTokenHandler bearerTokenHandler) {
        return new ItemClassDataLoader(itemClassService, bearerTokenHandler);
    }

    @Bean
    @Order(2)
    public CommandLineRunner itemSubclassDataLoader(ItemClassService itemClassService, ItemSubclassService itemSubclassService, BearerTokenHandler bearerTokenHandler) {
        return new ItemSubclassDataLoader(itemClassService, itemSubclassService, bearerTokenHandler);
    }
}
