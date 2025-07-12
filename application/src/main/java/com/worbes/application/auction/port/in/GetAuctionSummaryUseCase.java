package com.worbes.application.auction.port.in;

import com.worbes.application.auction.model.AuctionSummary;
import com.worbes.application.auction.port.out.AuctionSummarySearchCondition;

import java.util.List;

public interface GetAuctionSummaryUseCase {
    List<AuctionSummary> getSummary(AuctionSummarySearchCondition condition);
}
