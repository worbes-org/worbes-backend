package com.worbes.web.auction.model;

import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.model.QualityType;

import java.util.Map;

public record ItemResponse(
        Long id,
        Map<String, String> name,
        QualityType quality,
        InventoryType inventoryType,
        String icon,
        Integer craftingTier,
        String bonuses
) {
    public ItemResponse(Item item, String bonuses) {
        this(
                item.getId(),
                item.getName(),
                item.getQuality(),
                item.getInventoryType(),
                item.getIcon(),
                item.getCraftingTierValue(),
                bonuses
        );
    }
}
