package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;

import java.util.List;

public interface AuctionApiFetcher {
    List<Auction> fetch(RegionType region, Long realmId);

    List<Auction> fetch(RegionType region);
}
