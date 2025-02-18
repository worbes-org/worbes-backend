package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuctionServiceImplTest {

    @Test
    void fetchCommodities_ShouldReturnAuctionsForUSRegion() {
        AuctionService auctionService = new AuctionServiceImpl();
        List<Auction> auctions = auctionService.fetchCommodities(Region.US);

        assertNotNull(auctions);
    }
}
