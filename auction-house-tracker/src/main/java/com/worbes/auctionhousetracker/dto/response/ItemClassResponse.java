package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import lombok.Data;

import java.util.List;

@Data
public class ItemClassResponse {

    @JsonProperty("class_id")
    private Long id;

    private Language name;

    @JsonProperty("item_subclasses")
    private List<Subclass> subclassResponses;

    @Data
    public static class Subclass {

        private Long id;
        private Language name;
    }
}
