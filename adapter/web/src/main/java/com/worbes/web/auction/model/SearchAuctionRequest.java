package com.worbes.web.auction.model;

import com.worbes.application.realm.model.RegionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SearchAuctionRequest(
        @NotNull(message = "region must not be null") RegionType region,
        @NotNull(message = "reamId must not be null") Long realmId,
        Long itemClassId,
        @Size(max = 100) String itemName,
        Long itemSubclassId
) {
}
