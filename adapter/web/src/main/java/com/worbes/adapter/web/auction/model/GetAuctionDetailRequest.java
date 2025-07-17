package com.worbes.adapter.web.auction.model;

import com.worbes.application.realm.model.RegionType;
import jakarta.validation.constraints.NotNull;

public record GetAuctionDetailRequest(
        @NotNull(message = "region must not be null") RegionType region,
        @NotNull(message = "reamId must not be null") Long realmId,
        String itemBonus
) {
}
