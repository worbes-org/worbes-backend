package com.worbes.application.auction.port.out;

import com.worbes.application.auction.port.in.AuctionFetchResult;
import com.worbes.application.realm.model.RegionType;

import java.util.List;

public interface AuctionFetcher {
    List<AuctionFetchResult> fetch(RegionType region, Long realmId);
}
