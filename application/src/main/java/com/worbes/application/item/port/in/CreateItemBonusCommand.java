package com.worbes.application.item.port.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateItemBonusCommand(
        Long id,
        String name,
        Integer level,
        @JsonProperty("base_level") Integer baseLevel
) {
}
