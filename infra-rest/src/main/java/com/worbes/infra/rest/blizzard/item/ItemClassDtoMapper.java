package com.worbes.infra.rest.blizzard.item;

import com.worbes.domain.item.dto.ItemClassDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemClassDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ItemClassDto toDto(ItemClassesIndexResponse.ItemClass element);

    List<ItemClassDto> toDtoList(List<ItemClassesIndexResponse.ItemClass> elements);

}
