package com.worbes.application.auction.port.in;

import com.worbes.application.realm.model.RegionType;

public record GetAuctionItemStatsQuery(
        RegionType region,
        Long realmId,
        Long itemId,
        String itemBonus
) {
}
