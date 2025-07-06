package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;

import java.util.List;
import java.util.Set;

public interface AuctionWriteRepository {
    int upsertAll(List<Auction> auctions);

    Long updateEndedAtBy(RegionType region, Long realmId, Set<Long> auctionIds);
}
