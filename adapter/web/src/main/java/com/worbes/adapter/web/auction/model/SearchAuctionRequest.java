package com.worbes.adapter.web.auction.model;

import com.worbes.application.realm.model.RegionType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SearchAuctionRequest(
        @NotNull(message = "region must not be null") RegionType region,
        @NotNull(message = "reamId must not be null") Long realmId,
        @Size(max = 100) String name,
        Long classId,
        Long subclassId,
        @Min(1) Integer minQuality,
        @Max(6) Integer maxQuality,
        @Min(1) Integer minItemLevel,
        @Max(999) Integer maxItemLevel,
        @Min(1) @Max(11) Integer expansionId
) {
}
