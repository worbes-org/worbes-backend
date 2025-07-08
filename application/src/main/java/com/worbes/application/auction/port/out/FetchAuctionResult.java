package com.worbes.application.auction.port.out;

import com.worbes.application.realm.model.RegionType;

public record FetchAuctionResult(
        Long id,
        Long itemId,
        Integer quantity,
        Long buyout,
        RegionType region,
        Long realmId
) {
}
