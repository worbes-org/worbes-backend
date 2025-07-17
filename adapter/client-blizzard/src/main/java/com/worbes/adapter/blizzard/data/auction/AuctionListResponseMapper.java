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
            AuctionResponse response
    ) {
        Long price = response.buyout() != null ? response.buyout() : response.bid();
        return new Auction(
                response.id(),
                response.item().id(),
                realmId,
                response.quantity(),
                price,
                region,
                response.item().bonusList()
        );
    }
}
