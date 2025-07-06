package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.port.out.AuctionTrend;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface AuctionTrendViewMapper {

    @Mapping(source = "minPrice", target = "lowestPrice")
    AuctionTrend toDomain(AuctionTrendView entity);
}
