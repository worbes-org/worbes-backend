package com.worbes.application.item.model;

import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.common.model.LocalizedName;
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
    private final Long displayId;

    private Item(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.classId = builder.classId;
        this.subclassId = builder.subclassId;
        this.quality = builder.quality;
        this.level = builder.level;
        this.inventoryType = builder.inventoryType;
        this.isStackable = builder.isStackable;
        this.craftingTier = builder.craftingTier;
        this.icon = builder.icon;
        this.expansionId = builder.expansionId;
        this.displayId = builder.displayId;
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {
        private Long id;
        private LocalizedName name;
        private Long classId;
        private Long subclassId;
        private QualityType quality;
        private Integer level;
        private InventoryType inventoryType;
        private Boolean isStackable;
        private CraftingTierType craftingTier;
        private String icon;
        private Integer expansionId;
        private Long displayId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(Map<String, String> name) {
            this.name = LocalizedName.fromRaw(name);
            return this;
        }

        public Builder name(LocalizedName name) {
            this.name = name;
            return this;
        }

        public Builder classId(Long classId) {
            this.classId = classId;
            return this;
        }

        public Builder subclassId(Long subclassId) {
            this.subclassId = subclassId;
            return this;
        }

        public Builder quality(Integer quality) {
            this.quality = QualityType.fromValue(quality);
            return this;
        }

        public Builder quality(String quality) {
            this.quality = QualityType.valueOf(quality);
            return this;
        }

        public Builder quality(QualityType quality) {
            this.quality = quality;
            return this;
        }

        public Builder level(Integer level) {
            this.level = level;
            return this;
        }

        public Builder inventoryType(String inventoryType) {
            this.inventoryType = InventoryType.valueOf(inventoryType);
            return this;
        }

        public Builder inventoryType(InventoryType inventoryType) {
            this.inventoryType = inventoryType;
            return this;
        }

        public Builder isStackable(Boolean isStackable) {
            this.isStackable = isStackable;
            return this;
        }

        public Builder craftingTier(Integer craftingTier) {
            this.craftingTier = Optional.ofNullable(craftingTier)
                    .map(CraftingTierType::fromValue)
                    .orElse(null);
            return this;
        }

        public Builder craftingTier(CraftingTierType craftingTier) {
            this.craftingTier = craftingTier;
            return this;
        }

        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder expansionId(Integer expansionId) {
            this.expansionId = expansionId;
            return this;
        }

        public Builder displayId(Long displayId) {
            this.displayId = displayId;
            return this;
        }

        public Item build() {
            Objects.requireNonNull(id, "Item ID cannot be null");
            Objects.requireNonNull(name, "Item name cannot be null");
            Objects.requireNonNull(classId, "Class ID cannot be null");
            Objects.requireNonNull(subclassId, "Subclass ID cannot be null");
            Objects.requireNonNull(quality, "Item quality cannot be null");
            Objects.requireNonNull(level, "Item level cannot be null");
            Objects.requireNonNull(inventoryType, "Item InventoryType cannot be null");
            Objects.requireNonNull(icon, "Item Icon cannot be null");
            Objects.requireNonNull(isStackable, "Item isStackable cannot be null");
            Objects.requireNonNull(expansionId, "Item ExpansionId cannot be null");

            return new Item(this);
        }
    }
}
