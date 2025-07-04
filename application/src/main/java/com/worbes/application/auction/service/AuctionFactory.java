package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.out.FetchAuctionResult;
import com.worbes.application.auction.port.out.FetchCommodityResult;
import org.springframework.stereotype.Component;

@Component
public class AuctionFactory {
    public Auction create(FetchAuctionResult dto) {
        return Auction.builder()
                .id(dto.id())
                .itemId(dto.itemId())
                .quantity(dto.quantity())
                .price(dto.buyout())
                .region(dto.region())
                .realmId(dto.realmId())
                .build();
    }

    public Auction create(FetchCommodityResult dto) {
        return Auction.builder()
                .id(dto.id())
                .itemId(dto.itemId())
                .quantity(dto.quantity())
                .price(dto.unitPrice())
                .region(dto.region())
                .build();
    }
}
