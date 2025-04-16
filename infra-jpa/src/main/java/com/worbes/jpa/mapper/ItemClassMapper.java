package com.worbes.jpa.mapper;

import com.worbes.domain.item.ItemClass;
import com.worbes.domain.shared.LocalizedName;
import com.worbes.jpa.entity.ItemClassEntity;

public class ItemClassMapper {

    public static ItemClass toDomain(ItemClassEntity entity) {
        return new ItemClass(
                entity.getId(),
                LocalizedName.fromRaw(entity.getName())
        );
    }

    public static ItemClassEntity toEntity(ItemClass domain) {
        return new ItemClassEntity(
                domain.getId(),
                domain.getName().asRaw()
        );
    }
}
