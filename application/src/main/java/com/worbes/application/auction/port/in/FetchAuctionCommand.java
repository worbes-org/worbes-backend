package com.worbes.application.auction.port.in;

import com.worbes.application.realm.model.RegionType;

public record FetchAuctionCommand(RegionType region, Long realmId) {
}
