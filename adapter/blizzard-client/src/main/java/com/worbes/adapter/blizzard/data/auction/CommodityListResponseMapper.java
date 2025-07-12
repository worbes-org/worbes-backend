package com.worbes.adapter.blizzard.data.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface CommodityListResponseMapper {

    @Mapping(target = "region", source = "region")
    @Mapping(target = "id", source = "response.id")
    @Mapping(target = "itemId", source = "response.itemId")
    @Mapping(target = "quantity", source = "response.quantity")
    @Mapping(target = "price", source = "response.unitPrice")
    Auction toDomain(
            RegionType region,
            CommodityListResponse.CommodityResponse response
    );
}
