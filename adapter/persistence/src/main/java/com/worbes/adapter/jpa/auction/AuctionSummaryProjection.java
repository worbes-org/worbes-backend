package com.worbes.adapter.jpa.auction;

public record AuctionSummaryProjection(
        Long itemId,
        Long lowestPrice,
        Integer available,
        String bonus,
        Integer bonusLevel,
        Integer baseLevel
) {
}
