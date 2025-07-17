package com.worbes.application.auction.port.in;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;

import java.util.List;

public interface FetchAuctionUseCase {
    List<Auction> execute(RegionType region, Long realmId);
}
