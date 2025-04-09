package com.worbes.auctionhousetracker.application.sychronizer;

import com.worbes.auctionhousetracker.entity.enums.RegionType;

public interface AuctionSynchronizer {

    void synchronize(RegionType region);

    void synchronize(RegionType region, Long realmId);
}
