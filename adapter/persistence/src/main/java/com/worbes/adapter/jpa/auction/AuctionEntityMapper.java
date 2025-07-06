package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.model.Auction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface AuctionEntityMapper {

    @Mapping(source = "auctionId", target = "id")
    Auction toDomain(AuctionEntity entity);

    @Mapping(source = "id", target = "auctionId")
    AuctionEntity toEntity(Auction auction);
}
