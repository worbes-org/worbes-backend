package com.worbes.application.item.model;

import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.common.model.LocalizedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    private final CraftingTierType craftingTier;
    private final Boolean isStackable;
    @Setter
    private String icon;
    @Setter
    private Long expansionId;

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
            Long expansionId
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
}
