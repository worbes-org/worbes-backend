package com.worbes.infra.blizzard.adapter;

import com.worbes.application.core.item.dto.ItemClassDto;
import com.worbes.infra.blizzard.response.ItemClassesIndexResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemClassDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ItemClassDto toDto(ItemClassesIndexResponse.ItemClassIndexElement element);

    List<ItemClassDto> toDtoList(List<ItemClassesIndexResponse.ItemClassIndexElement> elements);

}
