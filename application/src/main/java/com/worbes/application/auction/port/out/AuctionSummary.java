package com.worbes.application.auction.port.out;

public record AuctionSummary(
        Long itemId,
        Long lowestPrice,
        Integer available
) {
}
