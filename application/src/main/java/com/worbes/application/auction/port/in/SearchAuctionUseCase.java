package com.worbes.application.auction.port.in;

import java.util.List;

public interface SearchAuctionUseCase {
    List<SearchAuctionSummaryResult> execute(SearchAuctionSummaryQuery query);
}
