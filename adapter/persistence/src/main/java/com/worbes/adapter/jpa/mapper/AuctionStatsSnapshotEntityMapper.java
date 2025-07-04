package com.worbes.adapter.jpa.mapper;

import com.worbes.adapter.jpa.entity.AuctionStatsSnapshotEntity;
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
