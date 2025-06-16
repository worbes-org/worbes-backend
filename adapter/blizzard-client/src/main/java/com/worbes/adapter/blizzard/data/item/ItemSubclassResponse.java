package com.worbes.adapter.blizzard.data.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ItemSubclassResponse {

    @JsonProperty("subclass_id")
    private Long id;

    @JsonProperty("class_id")
    private Long classId;

    @JsonProperty("display_name")
    private Map<String, String> displayName;

    @JsonProperty("verbose_name")
    private Map<String, String> verboseName;
}
