package com.worbes.auctionhousetracker.dto.mapper;

import com.worbes.auctionhousetracker.dto.response.ItemMediaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemSaveCommandMapper {

    @Mapping(source = "itemResponse.itemClassId", target = "itemClassId")
    @Mapping(source = "itemResponse.itemSubclassId", target = "itemSubclassId")
    @Mapping(source = "itemResponse.id", target = "id")
    @Mapping(source = "itemResponse.name", target = "name")
    @Mapping(source = "itemResponse.quality", target = "quality")
    @Mapping(source = "itemResponse.level", target = "level")
    @Mapping(source = "itemResponse.inventoryType", target = "inventoryType")
    @Mapping(source = "itemResponse.previewItem", target = "previewItem")
    @Mapping(source = "mediaResponse.iconUrl", target = "iconUrl")
    ItemSaveCommand toCommand(ItemMediaResponse itemMediaResponse);
}
