package com.worbes.application.auction.port.out;

import com.worbes.application.realm.model.RegionType;

public record AuctionTrendSearchCondition(
        RegionType region,
        Long realmId,
        Long itemId,
        String itemBonus,
        Integer daysAgo
) {
}
