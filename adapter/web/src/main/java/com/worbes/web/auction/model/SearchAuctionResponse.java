package com.worbes.web.auction.model;

import com.worbes.application.auction.model.AuctionSummary;
import com.worbes.application.item.model.CraftingTierType;
import lombok.Data;

import java.util.Map;

@Data
public class SearchAuctionResponse {

    private final Map<String, String> name;
    private final String iconUrl;
    private final CraftingTierType tier;
    private final Long available;
    private final PriceResponse lowestPrice;

    public SearchAuctionResponse(AuctionSummary auctionSummary) {
        name = auctionSummary.getItemName();
        iconUrl = auctionSummary.getIconUrl();
        tier = auctionSummary.getCraftingTier();
        lowestPrice = new PriceResponse(auctionSummary.getPrice());
        available = auctionSummary.getAvailable();
    }
}
