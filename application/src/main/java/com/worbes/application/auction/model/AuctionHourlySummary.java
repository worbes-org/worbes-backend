package com.worbes.application.auction.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuctionHourlySummary {
    private final LocalDateTime hour;
    private final Long available;
    private final Price lowestPrice;

    public AuctionHourlySummary(LocalDateTime hour, Long available, Long lowestPrice) {
        this.hour = hour;
        this.available = available;
        this.lowestPrice = new Price(lowestPrice);
    }
}
