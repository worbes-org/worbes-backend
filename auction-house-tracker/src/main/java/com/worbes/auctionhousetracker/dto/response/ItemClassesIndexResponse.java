package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import lombok.Data;

import java.util.List;

@Data
public class ItemClassesIndexResponse {

    @JsonProperty("item_classes")
    private List<ItemClasses> itemClasses;

    @Data
    public static class ItemClasses {
        private Long id;
        private Language name;
    }
}
