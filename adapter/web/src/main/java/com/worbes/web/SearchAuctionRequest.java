package com.worbes.web;

import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.realm.model.RegionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SearchAuctionRequest(
        @NotBlank RegionType region,
        @NotNull Long realmId,
        @NotBlank LocaleCode locale,
        Long itemClassId,
        String itemName,
        Long itemSubclassId
) {
}
