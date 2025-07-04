package com.worbes.application.auction.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuctionStatsSnapshot {
    private final LocalDateTime time;
    private final Long totalQuantity;
    private final Price lowestPrice;

    public AuctionStatsSnapshot(LocalDateTime time, Long totalQuantity, Long lowestPrice) {
        this.time = time;
        this.totalQuantity = totalQuantity;
        this.lowestPrice = new Price(lowestPrice);
    }
}
