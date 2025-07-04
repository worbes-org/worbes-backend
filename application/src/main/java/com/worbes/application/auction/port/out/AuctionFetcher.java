package com.worbes.application.auction.port.out;

import com.worbes.application.realm.model.RegionType;

import java.util.List;

public interface AuctionFetcher {
    List<FetchAuctionResult> fetchAuctions(RegionType region, Long realmId);

    List<FetchCommodityResult> fetchCommodities(RegionType region);
}
