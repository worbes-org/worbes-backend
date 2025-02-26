package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import com.worbes.auctionhousetracker.entity.enums.Quality;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemResponse {

    private Long id;

    private Language name;

    private Quality quality;

    private Long itemClassId;

    private Long itemSubclassId;

    private Integer level;

    private String previewItem;

    @JsonProperty("quality")
    private void unpackNestedQuality(Map<String, Object> quality) {
        this.quality = Quality.valueOf((String) quality.get("type"));
    }

    @JsonProperty("item_class")
    private void unpackNestedItemClass(Map<String, Object> itemClass) {
        this.itemClassId = ((Number) itemClass.get("id")).longValue();
    }

    @JsonProperty("item_subclass")
    private void unpackNestedItemSubclass(Map<String, Object> itemSubclass) {
        this.itemSubclassId = ((Number) itemSubclass.get("id")).longValue();
    }

    @JsonProperty("preview_item")
    private void unpackPreviewItem(Object previewItem) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.previewItem = mapper.writeValueAsString(previewItem);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert preview_item to JSON string", e);
        }
    }
}
