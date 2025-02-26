package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import com.worbes.auctionhousetracker.entity.enums.Quality;
import lombok.Data;

import java.util.Map;

@Data
public class ItemResponse {

    private Long id;

    private Language name;

    private Quality quality;

    private Long itemClassId;

    private Long itemSubclassId;

    private Integer level;

    @JsonProperty("preview_item")
    private Object previewItem;

    @JsonProperty("quality")
    private void unpackNestedQuality(Map<String, String> quality) {
        this.quality = Quality.valueOf(quality.get("type"));
    }

    @JsonProperty("item_class")
    private void unpackNestedItemClass(Map<String, String> itemClass) {
        this.itemClassId = Long.valueOf(itemClass.get("id"));
    }

    @JsonProperty("item_subclass")
    private void unpackNestedItemSubclass(Map<String, String> itemSubclass) {
        this.itemSubclassId = Long.valueOf(itemSubclass.get("id"));
    }
}
