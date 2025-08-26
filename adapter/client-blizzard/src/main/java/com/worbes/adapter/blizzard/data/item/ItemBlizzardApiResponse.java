package com.worbes.adapter.blizzard.data.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ItemBlizzardApiResponse(
        Long id,
        Map<String, String> name,
        Quality quality,
        @JsonProperty("inventory_type") InventoryType inventoryType,
        @JsonProperty("item_class") ItemClass itemClass,
        @JsonProperty("item_subclass") ItemSubclass itemSubclass,
        @JsonProperty("level") Integer level,
        @JsonProperty("is_stackable") Boolean isStackable
) {
    // 변환 헬퍼
    public String qualityType() {
        return quality.type();
    }

    public String inventoryTypeValue() {
        return inventoryType.type();
    }

    public Long classId() {
        return itemClass.id();
    }

    public Long subclassId() {
        return itemSubclass.id();
    }

    public record Quality(String type) {
    }

    public record InventoryType(String type) {
    }

    public record ItemClass(Long id) {
    }

    public record ItemSubclass(Long id) {
    }
}
