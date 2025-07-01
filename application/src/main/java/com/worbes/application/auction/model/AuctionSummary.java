package com.worbes.application.auction.model;

import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.Item;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
        Long rawPrice = getRawPrice(summaryResult.lowestUnitPrice(), summaryResult.lowestBuyout());
        this.price = new Price(rawPrice);
    }

    public String getItemName(LocaleCode localeCode) {
        return item.getName(localeCode);
    }

    public CraftingTierType getCraftingTier() {
        return item.getCraftingTier();
    }

    public String getIconUrl() {
        return item.getIconUrl();
    }

    private Long getRawPrice(Long unitPrice, Long buyout) {
        if (unitPrice != null && unitPrice > 0 && buyout == 0) {
            return unitPrice;
        } else if (buyout != null && buyout > 0 && unitPrice == 0) {
            return buyout;
        } else {
            log.error("buyout and unitPrice are both null or zero buyout = {}, unitPrice =  {}", buyout, unitPrice);
            throw new IllegalArgumentException("buyout and unitPrice are both null or zero buyout");
        }
    }
}
