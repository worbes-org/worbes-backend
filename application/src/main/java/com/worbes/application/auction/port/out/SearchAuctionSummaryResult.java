package com.worbes.application.auction.port.out;

public record SearchAuctionSummaryResult(
        Long itemId,
        Long lowestUnitPrice,
        Long lowestBuyout,
        Long available
) {
}
