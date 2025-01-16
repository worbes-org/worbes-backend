package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ItemClassesIndexResponse {

    @JsonProperty("item_classes")
    private List<ItemClassResponse> itemClasses;
}
