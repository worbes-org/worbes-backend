package com.worbes.application.auction.port.in;

import com.worbes.application.realm.model.RegionType;

public record AuctionFetchResult(
        Long id,
        Long itemId,
        Long quantity,
        Long buyout,
        Long unitPrice,
        RegionType region,
        Long realmId) {
}
