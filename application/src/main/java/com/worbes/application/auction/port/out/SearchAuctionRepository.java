package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.model.AuctionStatsSnapshot;
import com.worbes.application.auction.port.in.SearchAuctionCommand;
import com.worbes.application.realm.model.RegionType;

import java.util.List;
import java.util.Set;

public interface SearchAuctionRepository {
    List<SearchAuctionSummaryResult> searchSummaries(SearchAuctionCommand command, Set<Long> itemIds);

    List<Auction> findActiveAuctions(Long itemId, RegionType region, Long realmId);

    List<AuctionStatsSnapshot> findHourlySnapshots(Long itemId, RegionType region, Long realmId, Integer dayMinus);
}
