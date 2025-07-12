package com.worbes.scheduler.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemBonusJson {

    private Long id;

    private String name;

    private Integer level;

    @JsonProperty("base_level")
    private Integer baseLevel;
}
