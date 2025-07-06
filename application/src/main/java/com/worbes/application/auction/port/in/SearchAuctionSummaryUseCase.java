package com.worbes.application.auction.port.in;

import com.worbes.application.auction.port.out.AuctionSummary;

import java.util.List;

public interface SearchAuctionSummaryUseCase {
    List<AuctionSummary> searchSummaries(SearchAuctionSummaryCondition query);
}
