package com.worbes.adapter.jpa.auction;

import com.worbes.adapter.jpa.item.ItemMapper;
import com.worbes.application.auction.model.AuctionSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface AuctionSnapshotMapper {

    @Mapping(source = "item", target = "item")
    AuctionSnapshot toDomain(AuctionSnapshotEntity entity);

    @Mapping(source = "item", target = "item")
    AuctionSnapshotEntity toEntity(AuctionSnapshot auction);
}
