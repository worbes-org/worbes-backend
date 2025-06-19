package com.worbes.application.auction.port.in;

import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.item.model.Item;

import java.util.List;
import java.util.Map;

public interface SearchAuctionSummaryUseCase {
    Map<Item, SearchAuctionSummaryResult> searchSummaries(SearchAuctionCommand command, List<Item> items);
}
