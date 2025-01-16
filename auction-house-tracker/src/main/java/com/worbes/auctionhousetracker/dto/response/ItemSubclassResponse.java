package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import lombok.Data;

@Data
public class ItemSubclassResponse {

    @JsonProperty("subclass_id")
    private Long id;

    @JsonProperty("class_id")
    private Long classId;

    @JsonProperty("display_name")
    private Language displayName;

    @JsonProperty("verbose_name")
    private Language verboseName;
}
