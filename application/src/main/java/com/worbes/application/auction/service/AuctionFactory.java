package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.AuctionFetchResult;
import org.springframework.stereotype.Component;

@Component
public class AuctionFactory {
    public Auction create(AuctionFetchResult dto) {
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
