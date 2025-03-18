package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
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
