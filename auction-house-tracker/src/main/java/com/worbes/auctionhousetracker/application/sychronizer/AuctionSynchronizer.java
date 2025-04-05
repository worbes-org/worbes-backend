package com.worbes.auctionhousetracker.application.sychronizer;

import com.worbes.auctionhousetracker.entity.enums.Region;

public interface AuctionSynchronizer {

    void synchronize(Region region);

}
