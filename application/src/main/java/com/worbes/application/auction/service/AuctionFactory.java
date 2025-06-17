package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.out.FetchAuctionResult;
import org.springframework.stereotype.Component;

@Component
public class AuctionFactory {
    public Auction create(FetchAuctionResult dto) {
        return Auction.builder()
                .id(dto.id())
                .active(true)
                .itemId(dto.itemId())
                .quantity(dto.quantity())
                .buyout(dto.buyout())
                .unitPrice(dto.unitPrice())
                .region(dto.region())
                .realmId(dto.realmId())
                .build();
    }
}
