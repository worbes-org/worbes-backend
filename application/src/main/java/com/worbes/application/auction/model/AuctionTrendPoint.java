package com.worbes.application.auction.model;

import java.time.Instant;

public record AuctionTrendPoint(
        Instant time,
        Long lowestPrice,
        Integer totalQuantity
) {
    public AuctionTrendPoint(AuctionSnapshot snapshot) {
        this(snapshot.getTime(), snapshot.getLowestPrice(), snapshot.getTotalQuantity());
    }
}
