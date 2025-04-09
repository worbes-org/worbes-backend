package com.worbes.domain.item;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class Item {

    private final Long id;
    private final Map<String, String> name;
    private final ItemClass itemClass;
    private final ItemSubclass itemSubclass;
    private final QualityType quality;
    private final Integer level;
    private final InventoryType inventoryType;
    private final Object previewItem;
    private final String iconUrl;

    public static Item create(Long id,
                              Map<String, String> name,
                              ItemClass itemClass,
                              ItemSubclass itemSubclass,
                              QualityType quality,
                              Integer level,
                              InventoryType inventoryType,
                              Object previewItem,
                              String iconUrl) {
        return Item.builder()
                .id(id)
                .name(name)
                .itemClass(itemClass)
                .itemSubclass(itemSubclass)
                .quality(quality)
                .level(level)
                .inventoryType(inventoryType)
                .previewItem(previewItem)
                .iconUrl(iconUrl)
                .build();
    }
}
