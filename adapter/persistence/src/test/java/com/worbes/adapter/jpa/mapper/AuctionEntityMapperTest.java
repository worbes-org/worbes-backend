package com.worbes.adapter.jpa.mapper;

import com.worbes.adapter.jpa.entity.AuctionEntity;
import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.BDDAssertions.then;

@DisplayName("Unit::AuctionEntityMapper")
class AuctionEntityMapperTest {

    private final AuctionEntityMapper mapper = Mappers.getMapper(AuctionEntityMapper.class);

    @Test
    @DisplayName("AuctionEntity를 Auction 도메인 객체로 변환한다")
    void it_converts_entity_to_domain() {
        // Given
        AuctionEntity entity = AuctionEntity.builder()
                .auctionId(1001L)
                .itemId(200L)
                .realmId(300L)
                .quantity(10L)
                .unitPrice(5000L)
                .buyout(5500L)
                .region(RegionType.KR)
                .active(true)
                .build();

        // When
        Auction result = mapper.toDomain(entity);

        // Then
        then(result.getId()).isEqualTo(1001L);
        then(result.getItemId()).isEqualTo(200L);
        then(result.getRealmId()).isEqualTo(300L);
        then(result.getQuantity()).isEqualTo(10L);
        then(result.getUnitPrice()).isEqualTo(5000L);
        then(result.getBuyout()).isEqualTo(5500L);
        then(result.getRegion()).isEqualTo(RegionType.KR);
        then(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("Auction 도메인 객체를 AuctionEntity로 변환한다")
    void it_converts_domain_to_entity() {
        // Given
        Auction auction = Auction.builder()
                .id(1001L)
                .itemId(200L)
                .realmId(300L)
                .quantity(10L)
                .unitPrice(5000L)
                .buyout(5500L)
                .region(RegionType.KR)
                .active(true)
                .build();

        // When
        AuctionEntity result = mapper.toEntity(auction);

        // Then
        then(result.getAuctionId()).isEqualTo(1001L);
        then(result.getItemId()).isEqualTo(200L);
        then(result.getRealmId()).isEqualTo(300L);
        then(result.getQuantity()).isEqualTo(10L);
        then(result.getUnitPrice()).isEqualTo(5000L);
        then(result.getBuyout()).isEqualTo(5500L);
        then(result.getRegion()).isEqualTo(RegionType.KR);
        then(result.isActive()).isTrue();
        then(result.getId()).isNull(); // DB 생성 ID는 null이어야 함
    }
}
