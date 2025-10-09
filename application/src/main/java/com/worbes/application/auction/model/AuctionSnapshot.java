package com.worbes.application.auction.model;

import com.worbes.application.item.model.Item;
import com.worbes.application.realm.model.RegionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
public class AuctionSnapshot {

    @Getter
    private final Instant time;

    private final Item item;

    @Getter
    private final RegionType region;

    @Getter
    private final Long realmId;

    @Getter
    private final Integer totalQuantity;

    @Getter
    private final Long lowestPrice;

    @Getter
    private final List<Long> itemBonus;

    private final Integer bonusLevel;

    private final Integer baseLevel;

    @Getter
    private final String suffix;

    public long getItemId() {
        return item.getId();
    }

    public Integer getItemCraftingTier() {
        return item.getCraftingTierValue();
    }

    public Integer getItemLevel() {
        return Optional.ofNullable(baseLevel).orElse(item.getLevel()) +
                Optional.ofNullable(bonusLevel).orElse(0);
    }
}
