package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.SearchAuctionSummaryCondition;
import com.worbes.application.realm.model.RegionType;

import java.util.List;

public interface AuctionReadRepository {
    List<AuctionSummary> findAllSummaryByCondition(SearchAuctionSummaryCondition query);

    List<Auction> findAllActiveBy(Long itemId, RegionType region, Long realmId);

    List<AuctionTrend> findHourlyTrendBy(Long itemId, RegionType region, Long realmId, Integer dayMinus);
}
