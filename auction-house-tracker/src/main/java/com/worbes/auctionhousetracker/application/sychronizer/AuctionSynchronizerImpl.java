package com.worbes.auctionhousetracker.application.sychronizer;

import com.worbes.auctionhousetracker.application.fetcher.AuctionFetcher;
import com.worbes.auctionhousetracker.application.fetcher.ItemFetcher;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.service.AuctionService;
import com.worbes.auctionhousetracker.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionSynchronizerImpl implements AuctionSynchronizer {

    private final AuctionFetcher auctionFetcher;
    private final ItemFetcher itemFetcher;
    private final ItemService itemService;
    private final AuctionService auctionService;

    @Override
    public void synchronize(Region region) {

    }
}
