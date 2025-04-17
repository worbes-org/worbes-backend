package com.worbes.client.wow.item;

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
    private List<ItemSubclass> subclassResponses;

    @Data
    public static class ItemSubclass {
        private Long id;
        private Map<String, String> name;
    }
}
