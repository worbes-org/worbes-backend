package com.worbes.adapter.blizzard.data.auction;

import com.worbes.application.auction.port.in.AuctionFetchResult;
import com.worbes.application.realm.model.RegionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface AuctionListResponseMapper {

    @Mapping(target = "region", source = "region")
    @Mapping(target = "realmId", source = "realmId")
    @Mapping(target = "id", source = "response.id")
    @Mapping(target = "itemId", source = "response.itemId")
    @Mapping(target = "quantity", source = "response.quantity")
    @Mapping(target = "buyout", source = "response.buyout")
    @Mapping(target = "unitPrice", source = "response.unitPrice")
    AuctionFetchResult toDto(
            RegionType region,
            Long realmId,
            AuctionListResponse.AuctionResponse response
    );
}
