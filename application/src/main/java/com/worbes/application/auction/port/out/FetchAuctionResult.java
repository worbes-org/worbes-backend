package com.worbes.application.auction.port.out;

import com.worbes.application.realm.model.RegionType;

public record FetchAuctionResult(
        Long id,
        Long itemId,
        Long quantity,
        Long buyout,
        Long unitPrice,
        RegionType region,
        Long realmId) {
}
