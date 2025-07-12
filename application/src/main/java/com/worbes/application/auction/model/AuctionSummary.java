package com.worbes.application.auction.model;

import com.worbes.application.item.model.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@AllArgsConstructor
public class AuctionSummary {

    @Getter
    private final Item item;
    @Getter
    private final Long lowestPrice;
    @Getter
    private final Integer available;
    @Getter
    private final String bonuses;
    private final Integer bonusLevel;
    private final Integer baseLevel;

    public Integer getItemLevel() {
        int base = Optional.ofNullable(this.baseLevel).orElse(item.getLevel());
        int bonus = Optional.ofNullable(bonusLevel).orElse(0);
        return base + bonus;
    }
}
