package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.auctionhousetracker.entity.enums.InventoryType;
import com.worbes.auctionhousetracker.entity.enums.QualityType;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemResponse {

    private Long id;

    private Map<String, String> name;

    private QualityType quality;

    private InventoryType inventoryType;

    private Long itemClassId;

    private Long itemSubclassId;

    private Integer level;

    @JsonProperty("preview_item")
    private Map<String, Object> previewItem;

    @JsonProperty("quality")
    private void unpackNestedQuality(Map<String, Object> quality) {
        this.quality = QualityType.valueOf((String) quality.get("type"));
    }

    @JsonProperty("item_class")
    private void unpackNestedItemClass(Map<String, Object> itemClass) {
        this.itemClassId = ((Number) itemClass.get("id")).longValue();
    }

    @JsonProperty("item_subclass")
    private void unpackNestedItemSubclass(Map<String, Object> itemSubclass) {
        this.itemSubclassId = ((Number) itemSubclass.get("id")).longValue();
    }

    @JsonProperty("inventory_type")
    private void unpackNestedInventoryType(Map<String, Object> inventoryType) {
        this.inventoryType = InventoryType.valueOf((String) inventoryType.get("type"));
    }
}
