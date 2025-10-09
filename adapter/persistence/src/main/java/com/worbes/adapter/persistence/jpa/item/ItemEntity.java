package com.worbes.adapter.persistence.jpa.item;

import com.worbes.adapter.persistence.jpa.common.BaseEntity;
import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.Item;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    @Column(nullable = false)
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
    private Integer expansionId;

    @Column(name = "display_id")
    private Long displayId;

    public static ItemEntity from(Item item) {
        return new ItemEntity(
                item.getId(),
                item.getName(),
                item.getClassId(),
                item.getSubclassId(),
                item.getQuality(),
                item.getLevel(),
                item.getInventoryType(),
                item.getIcon(),
                item.getCraftingTier(),
                item.getIsStackable(),
                item.getExpansionId(),
                item.getDisplayId()
        );
    }

    public Item toDomain() {
        return Item.builder()
                .id(id)
                .name(name)
                .classId(classId)
                .subclassId(subclassId)
                .quality(quality)
                .level(level)
                .inventoryType(inventoryType)
                .icon(icon)
                .craftingTier(craftingTier)
                .isStackable(isStackable)
                .expansionId(expansionId)
                .displayId(displayId)
                .build();
    }
}
