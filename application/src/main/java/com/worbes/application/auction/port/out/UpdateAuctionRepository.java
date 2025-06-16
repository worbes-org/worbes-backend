package com.worbes.application.auction.port.out;

import com.worbes.application.realm.model.RegionType;

import java.util.Set;

public interface UpdateAuctionRepository {
    Long deactivateBy(RegionType region, Long realmId, Set<Long> auctionIds);
}
