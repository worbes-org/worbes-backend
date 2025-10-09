package com.worbes.application.auction.port.in;

import com.worbes.application.realm.model.RegionType;

import java.time.Instant;

public interface CreateAuctionSnapshotUseCase {
    int execute(RegionType region, Long realmId, Instant time);
}
