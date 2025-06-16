package com.worbes.adapter.blizzard.data.item;

import com.worbes.application.item.port.out.ItemClassIndexFetchResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = "spring")
public interface ItemClassIndexResponseMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ItemClassIndexFetchResult toDto(ItemClassesIndexResponse.ItemClass element);

    List<ItemClassIndexFetchResult> toDtoList(List<ItemClassesIndexResponse.ItemClass> elements);
}
