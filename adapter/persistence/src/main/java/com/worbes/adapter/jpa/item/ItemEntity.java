package com.worbes.adapter.jpa.item;

import com.worbes.adapter.jpa.common.BaseEntity;
import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.QualityType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.Map;

@Table(name = "item")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemEntity extends BaseEntity {

    @Id
    private Long id;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, String> name;

    @Column(nullable = false, name = "item_class_id")
    private Long itemClassId;

    @Column(nullable = false, name = "item_subclass_id")
    private Long itemSubclassId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QualityType quality;

    @Column(nullable = false)
    private Integer level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "inventory_type")
    private InventoryType inventoryType;

    @Column(nullable = false, name = "icon_url")
    private String iconUrl;

    @Column(name = "crafting_tier")
    private CraftingTierType craftingTier;

    @Builder
    private ItemEntity(
            Long id,
            Map<String, String> name,
            Long itemClassId,
            Long itemSubclassId,
            QualityType quality,
            Integer level,
            InventoryType inventoryType,
            String iconUrl,
            CraftingTierType craftingTier
    ) {
        this.id = id;
        this.name = name;
        this.itemClassId = itemClassId;
        this.itemSubclassId = itemSubclassId;
        this.quality = quality;
        this.level = level;
        this.inventoryType = inventoryType;
        this.iconUrl = iconUrl;
        this.craftingTier = craftingTier;
    }
}
