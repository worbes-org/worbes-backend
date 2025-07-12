package com.worbes.adapter.blizzard.data.item;

import com.worbes.application.item.model.Item;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface ItemResponseMapper {
    Item toDto(ItemResponse itemResponse);
}
