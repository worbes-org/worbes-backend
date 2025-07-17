package com.worbes.adapter.jpa.item;

import com.worbes.application.item.model.Item;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toDomain(ItemEntity entity);

    ItemEntity toEntity(Item item);
}
