package com.worbes.web;

import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.Item;
import lombok.Data;

@Data
public class SearchAuctionResponse {

    private final String name;
    private final String iconUrl;
    private final CraftingTierType tier;
    private final Long lowestPrice;
    private final Long available;

    public SearchAuctionResponse(Item item, SearchAuctionSummaryResult summary, LocaleCode locale) {
        name = item.getName(locale);
        iconUrl = item.getIconUrl();
        tier = item.getCraftingTier();
        lowestPrice = summary.lowestUnitPrice();
        available = summary.available();
    }
}
