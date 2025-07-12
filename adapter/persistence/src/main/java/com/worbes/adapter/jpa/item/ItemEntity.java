package com.worbes.adapter.jpa.item;

import com.worbes.adapter.jpa.common.BaseEntity;
import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.QualityType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Map;

@Table(name = "item")
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemEntity extends BaseEntity {

    @Id
    private Long id;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, String> name;

    @Column(nullable = false, name = "class_id")
    private Long classId;

    @Column(nullable = false, name = "subclass_id")
    private Long subclassId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QualityType quality;

    @Column(nullable = false)
    private Integer level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "inventory_type")
    private InventoryType inventoryType;

    @Column(nullable = false, name = "icon")
    private String icon;

    @Column(name = "crafting_tier")
    private CraftingTierType craftingTier;

    @Column(name = "is_stackable", nullable = false)
    private Boolean isStackable;

    @Column(name = "expansion_id")
    private Long expansionId;

    @Builder
    private ItemEntity(
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
        this.name = name;
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
}
