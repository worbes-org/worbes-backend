package com.worbes.adapter.blizzard.data.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface AuctionListResponseMapper {

    default Auction toDomain(
            RegionType region,
            Long realmId,
            AuctionListResponse.AuctionResponse response
    ) {
        Long price = response.getBuyout() != null ? response.getBuyout() : response.getBid();
        return new Auction(
                response.getId(),
                response.getItemId(),
                realmId,
                response.getQuantity(),
                price,
                region,
                response.getItemBonuses()
        );
    }
}
