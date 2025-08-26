package com.worbes.application.auction.port.in;

import com.worbes.application.auction.model.AuctionSnapshot;

import java.util.List;

public record SearchAuctionSummaryResult(
        Long itemId,
        List<Long> itemBonus,
        Integer itemLevel,
        Integer craftingTier,
        Long lowestPrice,
        Integer totalQuantity
) {
    public SearchAuctionSummaryResult(AuctionSnapshot snapshot) {
        this(
                snapshot.getItemId(),
                snapshot.getItemBonus(),
                snapshot.getItemLevel(),
                snapshot.getItemCraftingTier(),
                snapshot.getLowestPrice(),
                snapshot.getTotalQuantity()
        );
    }
}
