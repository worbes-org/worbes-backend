package com.worbes.auctionhousetracker.runner;


import com.worbes.auctionhousetracker.service.ItemClassService;
import com.worbes.auctionhousetracker.service.ItemSubclassService;
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
@Order(2)
public class ItemSubclassRunner implements CommandLineRunner {

    private final ItemClassService itemClassService;
    private final ItemSubclassService itemSubclassService;

    @Override
    public void run(String... args) {
        itemSubclassService.init(itemClassService.getAll());
    }
}
