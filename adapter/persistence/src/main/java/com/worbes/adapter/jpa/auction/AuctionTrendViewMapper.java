package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.model.AuctionTrendPoint;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface AuctionTrendViewMapper {
    AuctionTrendPoint toDomain(AuctionTrendView entity);
}
