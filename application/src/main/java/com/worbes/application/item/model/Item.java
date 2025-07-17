package com.worbes.application.item.model;

import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.common.model.LocalizedName;
import com.worbes.application.item.port.out.FetchExtraItemInfoResult;
import com.worbes.application.item.port.out.FetchItemApiResult;
import com.worbes.application.item.port.out.FindItemResult;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Getter
public class Item {

    private final Long id;
    private final LocalizedName name;
    private final Long classId;
    private final Long subclassId;
    private final QualityType quality;
    private final Integer level;
    private final InventoryType inventoryType;
    private final Boolean isStackable;
    private final CraftingTierType craftingTier;
    private final String icon;
    private final Integer expansionId;

    @Builder
    private Item(
            Long id,
            Map<String, String> name,
            Long classId,
            Long subclassId,
            QualityType quality,
            Integer level,
            InventoryType inventoryType,
            String icon,
            CraftingTierType craftingTier,
            Boolean isStackable,
            Integer expansionId
    ) {
        this.id = id;
        this.name = LocalizedName.fromRaw(name);
        this.classId = classId;
        this.subclassId = subclassId;
        this.quality = quality;
        this.level = level;
        this.inventoryType = inventoryType;
        this.icon = icon;
        this.craftingTier = craftingTier;
        this.isStackable = isStackable;
        this.expansionId = expansionId;
    }

    public static Item from(FetchItemApiResult item, FetchExtraItemInfoResult extra) {
        QualityType qualityType = Optional.of(item.quality()).map(QualityType::valueOf).get();
        InventoryType inventoryType = Optional.of(item.inventoryType()).map(InventoryType::valueOf).get();
        CraftingTierType craftingTierType = Optional.ofNullable(extra.craftingQualityTier())
                .map(CraftingTierType::fromValue)
                .orElse(null);
        return Item.builder()
                .id(item.id())
                .name(item.name())
                .classId(item.classId())
                .subclassId(item.subclassId())
                .quality(qualityType)
                .level(item.level())
                .inventoryType(inventoryType)
                .isStackable(item.isStackable())
                .icon(extra.icon())
                .craftingTier(craftingTierType)
                .expansionId(extra.expansion())
                .build();
    }

    public static Item from(FindItemResult result) {
        QualityType qualityType = Optional.of(result.quality()).map(QualityType::fromValue).get();
        InventoryType inventoryType = Optional.of(result.inventoryType()).map(InventoryType::valueOf).get();
        CraftingTierType craftingTierType = Optional.ofNullable(result.craftingTier())
                .map(CraftingTierType::fromValue)
                .orElse(null);
        return Item.builder()
                .id(result.id())
                .name(result.name())
                .classId(result.classId())
                .subclassId(result.subclassId())
                .quality(qualityType)
                .level(result.level())
                .inventoryType(inventoryType)
                .isStackable(result.isStackable())
                .icon(result.icon())
                .craftingTier(craftingTierType)
                .expansionId(result.expansionId())
                .build();
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

    public Integer getCraftingTierValue() {
        return Optional.ofNullable(craftingTier)
                .map(CraftingTierType::getValue)
                .orElse(null);
    }

    public Integer getItemLevel() {
        return this.level;
    }

    public Integer getItemLevel(Integer baseLevel, Integer bonusLevel) {
        return Optional.ofNullable(baseLevel).orElse(this.level) +
                Optional.ofNullable(bonusLevel).orElse(0);
    }
}
