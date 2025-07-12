package com.worbes.adapter.blizzard.data.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.QualityType;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemResponse {

    private Long id;
    private Map<String, String> name;
    private QualityType quality;
    private InventoryType inventoryType;
    private Long classId;
    private Long subclassId;
    private Integer level;

    @JsonProperty("is_stackable")
    private Boolean isStackable;

    @JsonProperty("quality")
    private void unpackNestedQuality(Map<String, Object> quality) {
        this.quality = QualityType.valueOf((String) quality.get("type"));
    }

    @JsonProperty("item_class")
    private void unpackNestedItemClass(Map<String, Object> itemClass) {
        this.classId = ((Number) itemClass.get("id")).longValue();
    }

    @JsonProperty("item_subclass")
    private void unpackNestedItemSubclass(Map<String, Object> itemSubclass) {
        this.subclassId = ((Number) itemSubclass.get("id")).longValue();
    }

    @JsonProperty("inventory_type")
    private void unpackNestedInventoryType(Map<String, Object> inventoryType) {
        this.inventoryType = InventoryType.valueOf((String) inventoryType.get("type"));
    }
}
