package com.worbes.adapter.jpa.item;

import com.worbes.adapter.jpa.common.BaseEntity;
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
@AllArgsConstructor
public class ItemBonusEntity extends BaseEntity {

    @Id
    private Long id;

    private String suffix;

    private Integer level;

    @Column(name = "base_level")
    private Integer baseLevel;
}
