package com.worbes.application.auction.model;

import java.time.Instant;
import java.util.List;

public record AuctionTrendPoint(
        Long id,
        Long itemId,
        List<Long> itemBonus,
        Instant time,
        Long lowestPrice,
        Integer totalQuantity
) {
}
