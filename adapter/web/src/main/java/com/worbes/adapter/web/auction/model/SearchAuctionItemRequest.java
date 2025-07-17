package com.worbes.adapter.web.auction.model;

import com.worbes.application.realm.model.RegionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SearchAuctionItemRequest(
        @NotNull(message = "region must not be null") RegionType region,
        @NotNull(message = "reamId must not be null") Long realmId,
        Long classId,
        @NotBlank @Size(max = 100) String name,
        Long subclassId
) {
}
