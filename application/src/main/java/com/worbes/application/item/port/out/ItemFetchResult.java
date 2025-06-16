package com.worbes.application.item.port.out;

import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.QualityType;

import java.util.Map;

public record ItemFetchResult(
        Long id,
        Map<String, String> name,
        QualityType quality,
        InventoryType inventoryType,
        Long itemClassId,
        Long itemSubclassId,
        Integer level,
        Object previewItem
) {
}
