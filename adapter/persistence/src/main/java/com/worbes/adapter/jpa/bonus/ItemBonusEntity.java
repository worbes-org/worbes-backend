package com.worbes.adapter.jpa.bonus;

import com.worbes.adapter.jpa.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "item_bonus")
@Entity
@Getter
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
