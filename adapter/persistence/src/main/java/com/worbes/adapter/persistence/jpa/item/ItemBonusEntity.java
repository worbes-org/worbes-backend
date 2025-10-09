package com.worbes.adapter.persistence.jpa.item;

import com.worbes.adapter.persistence.jpa.common.BaseEntity;
import com.worbes.application.item.model.ItemBonus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Table(name = "item_bonus")
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemBonusEntity extends BaseEntity {

    @Id
    private Long id;

    private String suffix;

    private Integer level;

    @Column(name = "base_level")
    private Integer baseLevel;

    public static ItemBonusEntity from(ItemBonus itemBonus) {
        return new ItemBonusEntity(
                itemBonus.id(),
                itemBonus.suffix(),
                itemBonus.level(),
                itemBonus.baseLevel()
        );
    }
}
