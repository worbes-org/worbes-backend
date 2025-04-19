package com.worbes.adapter.blizzard.data.item;

import com.worbes.application.initializer.ItemClassDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ItemClassDtoMapper {
    ItemClassDtoMapper INSTANCE = Mappers.getMapper(ItemClassDtoMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "subclassResponses", source = "subclassResponses")
    ItemClassDto toDto(ItemClassResponse response);

    List<ItemClassDto> toDtoList(List<ItemClassResponse> responses);

    ItemClassDto.ItemSubclass toDto(ItemClassResponse.ItemSubclass response);

    List<ItemClassDto.ItemSubclass> toDtoSubList(List<ItemClassResponse.ItemSubclass> responses);
}
