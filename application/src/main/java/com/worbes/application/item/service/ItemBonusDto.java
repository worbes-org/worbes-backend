package com.worbes.application.item.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ItemBonusDto(
        Long id,
        String name,
        Integer level,
        @JsonProperty("base_level") Integer baseLevel
) {
}
