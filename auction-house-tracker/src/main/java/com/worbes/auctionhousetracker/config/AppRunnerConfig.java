package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.service.ItemClassService;
import com.worbes.auctionhousetracker.service.ItemSubclassService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class AppRunnerConfig {

    @Bean
    @Order(1)
    public CommandLineRunner itemClassRunner(ItemClassService itemClassService, ItemSubclassService itemSubclassService) {
        return new ItemClassRunner(itemClassService, itemSubclassService);
    }
}
