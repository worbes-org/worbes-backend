package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.AuctionSummary;

import java.util.List;

public interface AuctionSummaryQueryRepository {
    List<AuctionSummary> findSummary(AuctionSummarySearchCondition condition);
}
