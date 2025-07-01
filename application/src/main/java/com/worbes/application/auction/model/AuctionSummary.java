package com.worbes.application.auction.model;

import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.Item;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class AuctionSummary {

    private final Item item;

    @Getter
    private final Price price;

    @Getter
    private final Long available;

    public AuctionSummary(Item item, SearchAuctionSummaryResult summaryResult) {
        this.item = item;
        this.available = summaryResult.available();
        this.price = new Price(summaryResult.minPrice());
    }

    public String getItemName(LocaleCode localeCode) {
        return item.getName(localeCode);
    }

    public Map<String, String> getItemName() {
        return item.getName();
    }

    public CraftingTierType getCraftingTier() {
        return item.getCraftingTier();
    }

    public String getIconUrl() {
        return item.getIconUrl();
    }
}
