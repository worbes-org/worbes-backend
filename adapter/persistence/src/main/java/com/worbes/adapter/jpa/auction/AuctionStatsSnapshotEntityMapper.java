package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.model.AuctionStatsSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface AuctionStatsSnapshotEntityMapper {

    @Mapping(source = "minPrice", target = "lowestPrice")
    AuctionStatsSnapshot toDomain(AuctionStatsSnapshotEntity entity);
}
