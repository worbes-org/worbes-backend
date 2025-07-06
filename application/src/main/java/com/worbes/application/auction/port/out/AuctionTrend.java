package com.worbes.application.auction.port.out;

import java.time.LocalDateTime;

public record AuctionTrend(
        LocalDateTime time,
        Integer totalQuantity,
        Long lowestPrice
) {
}
