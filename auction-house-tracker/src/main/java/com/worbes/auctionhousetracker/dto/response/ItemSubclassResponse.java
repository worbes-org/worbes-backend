package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.auctionhousetracker.entity.embeded.Translation;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemSubclassResponse {

    @JsonProperty("subclass_id")
    private Long id;

    @JsonProperty("class_id")
    private Long classId;

    @JsonProperty("display_name")
    private Translation displayName;

    @JsonProperty("verbose_name")
    private Translation verboseName;
}
