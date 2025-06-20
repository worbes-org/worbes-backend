package com.worbes.web;

import com.worbes.application.auction.model.AuctionSummary;
import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.item.model.CraftingTierType;
import lombok.Data;

@Data
public class SearchAuctionResponse {

    private final String name;
    private final String iconUrl;
    private final CraftingTierType tier;
    private final Long available;
    private final PriceResponse lowestPrice;

    public SearchAuctionResponse(AuctionSummary auctionSummary, LocaleCode locale) {
        name = auctionSummary.getItemName(locale);
        iconUrl = auctionSummary.getIconUrl();
        tier = auctionSummary.getCraftingTier();
        lowestPrice = new PriceResponse(auctionSummary.getPrice());
        available = auctionSummary.getAvailable();
    }
}
