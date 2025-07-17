package com.worbes.application.auction.model;

import com.worbes.application.item.model.Item;
import com.worbes.application.realm.model.RegionType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class AuctionSnapshot {

    private final Instant time;
    private final Item item;
    private final RegionType region;
    private final Long realmId;
    private final Integer totalQuantity;
    private final Long lowestPrice;
    private final String bonusList;
    private final Integer bonusLevel;
    private final Integer baseLevel;
    private final String suffix;

    @Builder
    private AuctionSnapshot(
            Instant time,
            Item item,
            RegionType region,
            Long realmId,
            Integer totalQuantity,
            Long lowestPrice,
            String bonusList,
            Integer bonusLevel,
            Integer baseLevel,
            String suffix
    ) {
        this.time = time;
        this.item = item;
        this.region = region;
        this.realmId = realmId;
        this.totalQuantity = totalQuantity;
        this.lowestPrice = lowestPrice;
        this.bonusList = bonusList;
        this.bonusLevel = bonusLevel;
        this.baseLevel = baseLevel;
        this.suffix = suffix;
    }
}
