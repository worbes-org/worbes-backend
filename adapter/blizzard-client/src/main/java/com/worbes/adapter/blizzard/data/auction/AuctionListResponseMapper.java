package com.worbes.adapter.blizzard.data.auction;

import com.worbes.application.auction.port.out.FetchAuctionResult;
import com.worbes.application.realm.model.RegionType;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface AuctionListResponseMapper {

    default FetchAuctionResult toDto(
            RegionType region,
            Long realmId,
            AuctionListResponse.AuctionResponse response
    ) {
        Long price = response.getBuyout() != null ? response.getBuyout() : response.getBid();
        return new FetchAuctionResult(
                response.getId(),
                response.getItemId(),
                response.getQuantity(),
                price,
                region,
                realmId
        );
    }
}
