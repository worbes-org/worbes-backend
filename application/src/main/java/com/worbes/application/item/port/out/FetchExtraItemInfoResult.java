package com.worbes.application.item.port.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FetchExtraItemInfoResult(
        String icon,
        Integer expansion,
        Integer craftingQualityTier,
        Long display
) {
}
