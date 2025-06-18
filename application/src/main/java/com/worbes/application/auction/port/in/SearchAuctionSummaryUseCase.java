package com.worbes.application.auction.port.in;

import com.worbes.application.auction.model.AuctionSummary;
import com.worbes.application.item.model.Item;

import java.util.List;

public interface SearchAuctionSummaryUseCase {
    List<AuctionSummary> searchSummaries(SearchAuctionCommand command, List<Item> items);
}
