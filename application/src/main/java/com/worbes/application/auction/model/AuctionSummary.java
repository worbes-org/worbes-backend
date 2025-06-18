package com.worbes.application.auction.model;

public record AuctionSummary(
        String name,
        Long lowestUnitPrice,
        Long lowestBuyout,
        Long available
) {
}
