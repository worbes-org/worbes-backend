package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;

import java.util.List;

public class AuctionServiceImpl implements AuctionService {
    @Override
    public List<Auction> fetchCommodities(Region region) {
        return List.of();
    }

    @Override
    public List<Auction> fetchAuctions(Region region, Integer realmId) {
        return null;
    }
}
