package com.worbes.application.auction.port.out;

import com.worbes.application.realm.model.RegionType;

public interface DeleteAuctionPort {
    long deleteAll(RegionType region, Long realmId);
}
