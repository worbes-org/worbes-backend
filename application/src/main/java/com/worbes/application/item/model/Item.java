package com.worbes.application.item.model;

import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.common.model.LocalizedName;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

@Getter
public class Item {

    private final Long id;
    private final LocalizedName name;
    private final Long itemClassId;
    private final Long itemSubclassId;
    private final QualityType quality;
    private final Integer level;
    private final InventoryType inventoryType;
    private final Object previewItem;
    private final String iconUrl;
    private final CraftingTierType craftingTier;

    @Builder
    private Item(
            Long id,
            Map<String, String> name,
            Long itemClassId,
            Long itemSubclassId,
            QualityType quality,
            Integer level,
            InventoryType inventoryType,
            Object previewItem,
            String iconUrl,
            CraftingTierType craftingTier
    ) {
        this.id = id;
        this.name = LocalizedName.fromRaw(name);
        this.itemClassId = itemClassId;
        this.itemSubclassId = itemSubclassId;
        this.quality = quality;
        this.level = level;
        this.inventoryType = inventoryType;
        this.previewItem = previewItem;
        this.iconUrl = iconUrl;
        this.craftingTier = craftingTier;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Map<String, String> getName() {
        return name.asRaw();
    }

    public String getName(LocaleCode locale) {
        return name.get(locale);
    }

    public boolean isCommodity() {
        return itemClassId == null;
    }
}
