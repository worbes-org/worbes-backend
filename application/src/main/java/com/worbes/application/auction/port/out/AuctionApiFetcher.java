package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;

import java.util.List;

public interface AuctionApiFetcher {
    List<Auction> fetchAuctions(RegionType region, Long realmId);

    List<Auction> fetchCommodities(RegionType region);
}
