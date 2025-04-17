package com.worbes.adapter.blizzard.data.item;

import com.worbes.application.initializer.ItemClassIndexDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ItemClassIndexDtoMapper {

    ItemClassIndexDtoMapper INSTANCE = Mappers.getMapper(ItemClassIndexDtoMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ItemClassIndexDto toDto(ItemClassesIndexResponse.ItemClass element);

    List<ItemClassIndexDto> toDtoList(List<ItemClassesIndexResponse.ItemClass> elements);
}
