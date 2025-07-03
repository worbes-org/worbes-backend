package com.worbes.application.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AuctionHistory {
    private LocalDateTime time;
    private Long totalQuantity;
    private Long minPrice;
}
