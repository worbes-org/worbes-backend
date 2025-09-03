package com.worbes.application.auction.port.in;

import com.worbes.application.realm.model.RegionType;

import java.util.List;

public record GetAuctionTrendQuery(
        RegionType region,
        Long realmId,
        Long itemId,
        List<Long> itemBonus,
        Integer daysAgo
) {
}
