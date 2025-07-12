package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.model.AuctionSummary;
import com.worbes.application.item.model.Item;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

@Primary
@Mapper(componentModel = "spring")
public interface AuctionSummaryProjectionMapper {

    default AuctionSummary toDomain(AuctionSummaryProjection projection, Item item) {
        if (projection == null || item == null) return null;
        return new AuctionSummary(
                item,
                projection.lowestPrice(),
                projection.available(),
                projection.bonus(),
                projection.bonusLevel(),
                projection.baseLevel()
        );
    }
}
