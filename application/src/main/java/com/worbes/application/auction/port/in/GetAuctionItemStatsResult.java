package com.worbes.application.auction.port.in;

import java.util.Map;

public record GetAuctionItemStatsResult(
        Long lowestPrice,
        Integer totalQuantity,
        Map<Long, Integer> quantityByPrice
) {
}
