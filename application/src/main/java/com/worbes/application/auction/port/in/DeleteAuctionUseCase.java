package com.worbes.application.auction.port.in;

import com.worbes.application.realm.model.RegionType;

public interface DeleteAuctionUseCase {
    long execute(RegionType region, Long realmId);
}
