package com.worbes.adapter.blizzard.data.item;

import com.worbes.application.item.port.out.ItemSubclassFetchResult;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface ItemSubclassResponseMapper {
    ItemSubclassFetchResult toDto(ItemSubclassResponse response);
}
