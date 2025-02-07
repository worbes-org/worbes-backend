package com.worbes.auctionhousetracker.runner;

import com.worbes.auctionhousetracker.service.ItemClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("!test")
@Order(1)
public class ItemClassRunner implements CommandLineRunner {

    private final ItemClassService itemClassService;

    @Override
    public void run(String... args) {
        itemClassService.init();
    }
}
