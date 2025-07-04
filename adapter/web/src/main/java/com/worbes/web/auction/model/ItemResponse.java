package com.worbes.web.auction.model;

import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.model.QualityType;

import java.util.Map;

public record ItemResponse(
        Long id,
        Map<String, String> name,
        QualityType quality,
        String iconUrl,
        CraftingTierType craftingTier
) {
    public ItemResponse(Item item) {
        this(item.getId(), item.getName(), item.getQuality(), item.getIconUrl(), item.getCraftingTier());
    }
}
