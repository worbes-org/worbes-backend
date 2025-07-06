package com.worbes.web.auction.model;

import com.worbes.application.auction.port.out.AuctionSummary;
import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.Item;
import lombok.Data;

import java.util.Map;
import java.util.Optional;

@Data
public class SearchAuctionSummaryResponse {

    private final Map<String, String> name;
    private final String iconUrl;
    private final Integer tier;
    private final Integer available;
    private final Long lowestPrice;

    public SearchAuctionSummaryResponse(Item item, AuctionSummary auctionSummary) {
        name = item.getName();
        iconUrl = item.getIconUrl();
        tier = Optional.ofNullable(item.getCraftingTier())
                .map(CraftingTierType::getValue)
                .orElse(null);
        lowestPrice = auctionSummary.lowestPrice();
        available = auctionSummary.available();
    }
}
