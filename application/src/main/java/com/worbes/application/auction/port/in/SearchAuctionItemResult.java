package com.worbes.application.auction.port.in;

public record SearchAuctionItemResult(
        Long itemId,
        String itemBonus,
        Integer itemLevel,
        Integer craftingTier,
        Long lowestPrice,
        Integer totalQuantity
) {
}
