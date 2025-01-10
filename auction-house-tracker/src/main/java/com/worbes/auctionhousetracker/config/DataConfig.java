package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.service.ItemClassService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConfig {

    @Bean
    public CommandLineRunner itemClassDataLoader(ItemClassService itemClassService, BearerTokenHandler bearerTokenHandler) {
        return new ItemClassDataLoader(itemClassService, bearerTokenHandler);
    }
}
