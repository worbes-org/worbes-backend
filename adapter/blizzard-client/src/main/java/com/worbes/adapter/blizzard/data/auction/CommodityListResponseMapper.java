package com.worbes.adapter.blizzard.data.auction;

import com.worbes.application.auction.port.out.FetchCommodityResult;
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
    @Mapping(target = "unitPrice", source = "response.unitPrice")
    FetchCommodityResult toDto(
            RegionType region,
            CommodityListResponse.CommodityResponse response
    );
}
