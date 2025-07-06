package com.worbes.application.auction.port.in;

import com.worbes.application.realm.model.RegionType;

import java.util.Set;

public interface EndAuctionUseCase {
    Long end(RegionType region, Long realmId, Set<Long> auctionIds);
}
