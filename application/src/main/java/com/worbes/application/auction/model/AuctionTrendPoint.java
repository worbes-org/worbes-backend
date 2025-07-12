package com.worbes.application.auction.model;

import java.time.Instant;

public record AuctionTrendPoint(
        Instant time,
        Integer totalQuantity,
        Long lowestPrice
) {
}
