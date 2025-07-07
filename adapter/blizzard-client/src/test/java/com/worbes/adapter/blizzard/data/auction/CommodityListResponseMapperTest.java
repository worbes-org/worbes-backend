package com.worbes.adapter.blizzard.data.auction;

import com.worbes.application.auction.port.out.FetchCommodityResult;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.BDDAssertions.then;

class CommodityListResponseMapperTest {

    private final CommodityListResponseMapper mapper = Mappers.getMapper(CommodityListResponseMapper.class);

    @Test
    @DisplayName("CommodityResponse를 FetchCommodityResult로 매핑한다")
    void shouldMapSingleResponseToDto() {
        // given
        RegionType region = RegionType.KR;

        CommodityListResponse.CommodityResponse response = new CommodityListResponse.CommodityResponse();
        response.setId(111222333L);
        response.setItemId(98765L);
        response.setQuantity(50L);
        response.setUnitPrice(12000L);

        // when
        FetchCommodityResult result = mapper.toDto(region, response);

        // then
        then(result.region()).isEqualTo(region);
        then(result.id()).isEqualTo(111222333L);
        then(result.itemId()).isEqualTo(98765L);
        then(result.quantity()).isEqualTo(50L);
        then(result.unitPrice()).isEqualTo(12000L);
    }
}
