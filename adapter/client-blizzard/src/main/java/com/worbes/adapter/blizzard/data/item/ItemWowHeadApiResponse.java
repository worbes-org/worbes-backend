package com.worbes.adapter.blizzard.data.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ItemWowHeadApiResponse(
        String icon,
        Integer expansion,
        Integer craftingQualityTier,
        Long display
) {
}
