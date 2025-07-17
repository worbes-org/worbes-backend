package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

class AuctionMapperTest {

    private final AuctionMapper mapper = Mappers.getMapper(AuctionMapper.class);

    @Test
    @DisplayName("AuctionEntity를 Auction 도메인 객체로 변환한다")
    void it_converts_entity_to_domain() {
        // Given
        AuctionEntity entity = AuctionEntity.builder()
                .id(1001L)
                .itemId(200L)
                .realmId(300L)
                .quantity(10)
                .price(5000L)
                .region(RegionType.KR)
                .build();

        // When
        Auction result = mapper.toDomain(entity);

        // Then
        then(result.getId()).isEqualTo(1001L);
        then(result.getItemId()).isEqualTo(200L);
        then(result.getRealmId()).isEqualTo(300L);
        then(result.getQuantity()).isEqualTo(10L);
        then(result.getPrice()).isEqualTo(5000L);
        then(result.getRegion()).isEqualTo(RegionType.KR);
    }

    @Test
    @DisplayName("Auction 도메인 객체를 AuctionEntity로 변환한다")
    void it_converts_domain_to_entity() {
        // Given
        Auction auction = Auction.builder()
                .id(1001L)
                .itemId(200L)
                .realmId(300L)
                .quantity(10)
                .price(5000L)
                .region(RegionType.KR)
                .itemBonus(List.of(1L, 2L, 3L))
                .build();

        // When
        AuctionEntity result = mapper.toEntity(auction);

        // Then
        then(result.getId()).isEqualTo(1001L);
        then(result.getItemId()).isEqualTo(200L);
        then(result.getRealmId()).isEqualTo(300L);
        then(result.getQuantity()).isEqualTo(10L);
        then(result.getPrice()).isEqualTo(5000L);
        then(result.getRegion()).isEqualTo(RegionType.KR);
        then(result.getEndedAt()).isNull();
    }
}
