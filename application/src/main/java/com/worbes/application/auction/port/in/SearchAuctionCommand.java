package com.worbes.application.auction.port.in;

import com.worbes.application.realm.model.RegionType;

public record SearchAuctionCommand(
        RegionType region,
        Long realmId
) {
}
