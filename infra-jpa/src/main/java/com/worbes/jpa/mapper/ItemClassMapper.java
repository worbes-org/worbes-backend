package com.worbes.jpa.mapper;

import com.worbes.domain.item.ItemClass;
import com.worbes.jpa.entity.ItemClassEntity;

public class ItemClassMapper {
    public static ItemClass toDomain(ItemClassEntity entity) {
        return ItemClass.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public static ItemClassEntity toEntity(ItemClass domain) {
        return ItemClassEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}
