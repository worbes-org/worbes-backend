package com.worbes.web.auction.model;

import com.worbes.application.auction.model.AuctionSummary;
import lombok.Data;

import java.util.Map;
import java.util.Optional;

@Data
public class SearchAuctionResponse {

    private final Map<String, String> name;
    private final String iconUrl;
    private final Integer tier;
    private final Long available;
    private final PriceResponse lowestPrice;

    public SearchAuctionResponse(AuctionSummary auctionSummary) {
        name = auctionSummary.getItemName();
        iconUrl = auctionSummary.getIconUrl();
        tier = Optional.ofNullable(auctionSummary.getCraftingTier()).isPresent() ? auctionSummary.getCraftingTier().getValue() : null;
        lowestPrice = new PriceResponse(auctionSummary.getPrice());
        available = auctionSummary.getAvailable();
    }
}
