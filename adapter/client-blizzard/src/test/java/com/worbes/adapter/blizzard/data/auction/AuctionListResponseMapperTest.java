package com.worbes.adapter.blizzard.data.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.BDDAssertions.then;


class AuctionListResponseMapperTest {

    private final AuctionListResponseMapper mapper = Mappers.getMapper(AuctionListResponseMapper.class);

    @Test
    @DisplayName("단일 AuctionResponse를 AuctionFetchResult로 매핑한다")
    void shouldMapSingleResponseToDomain() {
        // given
        RegionType region = RegionType.KR;
        Long realmId = 101L;

        AuctionListResponse.AuctionResponse response = new AuctionListResponse.AuctionResponse();
        response.setId(123456789L);
        response.setItemId(98765L);
        response.setQuantity(20);
        response.setBuyout(25000L);

        // when
        Auction result = mapper.toDomain(region, realmId, response);

        // then
        then(result.getRegion()).isEqualTo(region);
        then(result.getRealmId()).isEqualTo(realmId);
        then(result.getId()).isEqualTo(123456789L);
        then(result.getItemId()).isEqualTo(98765L);
        then(result.getQuantity()).isEqualTo(20);
        then(result.getPrice()).isEqualTo(25000L);
    }

    @Test
    @DisplayName("단일 AuctionResponse를 AuctionFetchResult로 매핑한다 (buyout 우선)")
    void shouldMapSingleResponseToDomain_buyout() {
        // given
        RegionType region = RegionType.KR;
        Long realmId = 101L;

        AuctionListResponse.AuctionResponse response = new AuctionListResponse.AuctionResponse();
        response.setId(123456789L);
        response.setItemId(98765L);
        response.setQuantity(20);
        response.setBuyout(25000L);
        response.setBid(10000L);

        // when
        Auction result = mapper.toDomain(region, realmId, response);

        // then
        then(result.getRegion()).isEqualTo(region);
        then(result.getRealmId()).isEqualTo(realmId);
        then(result.getId()).isEqualTo(123456789L);
        then(result.getItemId()).isEqualTo(98765L);
        then(result.getQuantity()).isEqualTo(20);
        then(result.getPrice()).isEqualTo(25000L); // buyout 우선
    }

    @Test
    @DisplayName("buyout이 null이면 bid를 price로 매핑한다")
    void shouldMapBidWhenBuyoutIsNull() {
        // given
        RegionType region = RegionType.KR;
        Long realmId = 101L;

        AuctionListResponse.AuctionResponse response = new AuctionListResponse.AuctionResponse();
        response.setId(222222L);
        response.setItemId(33333L);
        response.setQuantity(5);
        response.setBuyout(null);
        response.setBid(5555L);

        // when
        Auction result = mapper.toDomain(region, realmId, response);

        // then
        then(result.getRegion()).isEqualTo(region);
        then(result.getRealmId()).isEqualTo(realmId);
        then(result.getId()).isEqualTo(222222L);
        then(result.getItemId()).isEqualTo(33333L);
        then(result.getQuantity()).isEqualTo(5);
        then(result.getPrice()).isEqualTo(5555L); // buyout이 null이면 bid 사용
    }
}
