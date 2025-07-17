package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.model.Auction;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface AuctionMapper {

    Auction toDomain(AuctionEntity entity);

    AuctionEntity toEntity(Auction auction);
}
