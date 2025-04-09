package com.worbes.infra.rest.blizzard.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ItemClassResponse {

    @JsonProperty("class_id")
    private Long id;
    private Map<String, String> name;

    @JsonProperty("item_subclasses")
    private List<ItemSubclassDto> subclassResponses;

    @Data
    public static class ItemSubclassDto {
        private Long id;
        private Map<String, String> name;
    }
}
