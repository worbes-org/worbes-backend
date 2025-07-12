package com.worbes.web.auction.model;

import com.worbes.application.auction.model.AuctionSummary;
import lombok.Data;

@Data
public class SearchAuctionSummaryResponse {

    private final ItemResponse item;
    private final Integer itemLevel;
    private final Integer available;
    private final Long lowestPrice;

    public SearchAuctionSummaryResponse(AuctionSummary auctionSummary) {
        itemLevel = auctionSummary.getItemLevel();
        lowestPrice = auctionSummary.getLowestPrice();
        available = auctionSummary.getAvailable();
        item = new ItemResponse(auctionSummary.getItem(), auctionSummary.getBonuses());
    }
}
