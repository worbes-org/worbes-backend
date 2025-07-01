package com.worbes.web;

import com.worbes.application.realm.model.RegionType;

public record SearchAuctionRequest(
        RegionType region,
        Long realmId,
        Long itemClassId,
        String itemName,
        Long itemSubclassId
) {
}
