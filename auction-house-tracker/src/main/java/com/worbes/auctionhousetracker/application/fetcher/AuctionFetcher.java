package com.worbes.auctionhousetracker.application.fetcher;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;


public interface AuctionFetcher {

    AuctionResponse fetchCommodities(Region region);

    AuctionResponse fetchAuctions(Region region, Realm realm);
}
