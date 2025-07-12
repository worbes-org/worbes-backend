package com.worbes.adapter.blizzard.data.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.BDDAssertions.then;

class CommodityListResponseMapperTest {

    private final CommodityListResponseMapper mapper = Mappers.getMapper(CommodityListResponseMapper.class);

    @Test
    @DisplayName("CommodityResponse를 FetchCommodityResult로 매핑한다")
    void shouldMapSingleResponseToDomain() {
        // given
        RegionType region = RegionType.KR;

        CommodityListResponse.CommodityResponse response = new CommodityListResponse.CommodityResponse();
        response.setId(111222333L);
        response.setItemId(98765L);
        response.setQuantity(50);
        response.setUnitPrice(12000L);

        // when
        Auction result = mapper.toDomain(region, response);

        // then
        then(result.getRegion()).isEqualTo(region);
        then(result.getId()).isEqualTo(111222333L);
        then(result.getItemId()).isEqualTo(98765L);
        then(result.getQuantity()).isEqualTo(50);
        then(result.getPrice()).isEqualTo(12000L);
    }
}
