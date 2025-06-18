package com.worbes.application.auction.port.out;

import com.worbes.application.auction.port.in.SearchAuctionCommand;

import java.util.List;
import java.util.Set;

public interface SearchAuctionRepository {
    List<SearchAuctionSummaryResult> searchSummaries(SearchAuctionCommand command, Set<Long> itemIds);
}
