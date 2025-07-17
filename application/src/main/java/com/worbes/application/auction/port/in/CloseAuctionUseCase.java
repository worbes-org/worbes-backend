package com.worbes.application.auction.port.in;

import com.worbes.application.realm.model.RegionType;

import java.util.Set;

public interface CloseAuctionUseCase {
    long execute(RegionType region, Long realmId, Set<Long> auctionIds);
}
