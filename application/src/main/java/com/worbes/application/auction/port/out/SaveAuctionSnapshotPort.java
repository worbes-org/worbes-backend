package com.worbes.application.auction.port.out;

import com.worbes.application.realm.model.RegionType;

import java.time.Instant;

public interface SaveAuctionSnapshotPort {
    int save(RegionType region, Long realmId, Instant time);
}
