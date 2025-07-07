package com.worbes.adapter.blizzard.data.auction;

import com.worbes.application.auction.port.out.FetchAuctionResult;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.BDDAssertions.then;


class AuctionListResponseMapperTest {

    private final AuctionListResponseMapper mapper = Mappers.getMapper(AuctionListResponseMapper.class);

    @Test
    @DisplayName("단일 AuctionResponse를 AuctionFetchResult로 매핑한다")
    void shouldMapSingleResponseToDto() {
        // given
        RegionType region = RegionType.KR;
        Long realmId = 101L;

        AuctionListResponse.AuctionResponse response = new AuctionListResponse.AuctionResponse();
        response.setId(123456789L);
        response.setItemId(98765L);
        response.setQuantity(20L);
        response.setBuyout(25000L);

        // when
        FetchAuctionResult result = mapper.toDto(region, realmId, response);

        // then
        then(result.region()).isEqualTo(region);
        then(result.realmId()).isEqualTo(realmId);
        then(result.id()).isEqualTo(123456789L);
        then(result.itemId()).isEqualTo(98765L);
        then(result.quantity()).isEqualTo(20);
        then(result.buyout()).isEqualTo(25000L);
    }

    @Test
    @DisplayName("단일 AuctionResponse를 AuctionFetchResult로 매핑한다 (buyout 우선)")
    void shouldMapSingleResponseToDto_buyout() {
        // given
        RegionType region = RegionType.KR;
        Long realmId = 101L;

        AuctionListResponse.AuctionResponse response = new AuctionListResponse.AuctionResponse();
        response.setId(123456789L);
        response.setItemId(98765L);
        response.setQuantity(20L);
        response.setBuyout(25000L);
        response.setBid(10000L);

        // when
        FetchAuctionResult result = mapper.toDto(region, realmId, response);

        // then
        then(result.region()).isEqualTo(region);
        then(result.realmId()).isEqualTo(realmId);
        then(result.id()).isEqualTo(123456789L);
        then(result.itemId()).isEqualTo(98765L);
        then(result.quantity()).isEqualTo(20);
        then(result.buyout()).isEqualTo(25000L); // buyout 우선
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
        response.setQuantity(5L);
        response.setBuyout(null);
        response.setBid(5555L);

        // when
        FetchAuctionResult result = mapper.toDto(region, realmId, response);

        // then
        then(result.region()).isEqualTo(region);
        then(result.realmId()).isEqualTo(realmId);
        then(result.id()).isEqualTo(222222L);
        then(result.itemId()).isEqualTo(33333L);
        then(result.quantity()).isEqualTo(5);
        then(result.buyout()).isEqualTo(5555L); // buyout이 null이면 bid 사용
    }
}
