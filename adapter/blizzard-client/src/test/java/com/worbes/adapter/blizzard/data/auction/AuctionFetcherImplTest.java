package com.worbes.adapter.blizzard.data.auction;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.application.auction.port.out.FetchAuctionResult;
import com.worbes.application.auction.port.out.FetchCommodityResult;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@DisplayName("Unit::AuctionFetcherImpl")
@ExtendWith(MockitoExtension.class)
class AuctionFetcherImplTest {

    private final RegionType region = RegionType.KR;

    @Mock
    private BlizzardApiClient apiClient;

    @Mock
    private AuctionListResponseMapper auctionListResponseMapper;

    @Mock
    private CommodityListResponseMapper commodityListResponseMapper;

    @Mock
    private BlizzardApiUriFactory uriFactory;

    @InjectMocks
    private AuctionFetcherImpl auctionFetcher;

    @Test
    @DisplayName("경매장 URL을 생성하여 데이터를 fetch하고 매핑한다")
    void shouldFetchAuctionsWithRealmId() {
        Long realmId = 1234L;
        URI expectedUri = URI.create("https://some.api/auction");
        AuctionListResponse mockResponse = mock(AuctionListResponse.class);
        AuctionListResponse.AuctionResponse auction1 = createAuctionResponse(1L, 100L, 10L, 500L);
        AuctionListResponse.AuctionResponse auction2 = createAuctionResponse(2L, 200L, 20L, 1000L);

        given(uriFactory.auctionUri(region, realmId)).willReturn(expectedUri);
        given(apiClient.fetch(expectedUri, AuctionListResponse.class)).willReturn(mockResponse);
        given(mockResponse.getAuctions()).willReturn(List.of(auction1, auction2));

        FetchAuctionResult dto1 = mock(FetchAuctionResult.class, "dto1");
        FetchAuctionResult dto2 = mock(FetchAuctionResult.class, "dto2");
        given(auctionListResponseMapper.toDto(region, realmId, auction1)).willReturn(dto1);
        given(auctionListResponseMapper.toDto(region, realmId, auction2)).willReturn(dto2);

        // when
        List<FetchAuctionResult> results = auctionFetcher.fetchAuctions(region, realmId);

        // then
        then(uriFactory).should().auctionUri(region, realmId);
        then(apiClient).should().fetch(expectedUri, AuctionListResponse.class);
        then(auctionListResponseMapper).should(times(2)).toDto(eq(region), eq(realmId), any());
        assertThat(results).containsExactly(dto1, dto2);
    }

    @Test
    @DisplayName("RegionType에 따른 CommodityList를 FetchCommodityResult 리스트로 변환한다")
    void shouldFetchAndMapCommodities() {
        // given
        RegionType region = RegionType.KR;
        URI fakeUri = URI.create("https://test.com/commodity");
        Long id = 1001L;
        Long itemId = 5555L;
        Long quantity = 30L;
        Long unitPrice = 12000L;

        given(uriFactory.commodityUri(region)).willReturn(fakeUri);

        CommodityListResponse.CommodityResponse commodityResponse = new CommodityListResponse.CommodityResponse();
        commodityResponse.setId(id);
        commodityResponse.setItemId(itemId);
        commodityResponse.setQuantity(quantity);
        commodityResponse.setUnitPrice(unitPrice);

        CommodityListResponse commodityListResponse = new CommodityListResponse();
        commodityListResponse.setAuctions(List.of(commodityResponse));

        given(apiClient.fetch(fakeUri, CommodityListResponse.class)).willReturn(commodityListResponse);

        FetchCommodityResult expectedResult = new FetchCommodityResult(
                id,
                itemId,
                quantity,
                unitPrice,
                region
        );

        given(commodityListResponseMapper.toDto(region, commodityResponse)).willReturn(expectedResult);

        // when
        List<FetchCommodityResult> results = auctionFetcher.fetchCommodities(region);

        // then
        assertThat(results).hasSize(1);
        FetchCommodityResult result = results.get(0);
        assertThat(result.region()).isEqualTo(region);
        assertThat(result.id()).isEqualTo(1001L);
        assertThat(result.itemId()).isEqualTo(5555L);
        assertThat(result.quantity()).isEqualTo(30L);
        assertThat(result.unitPrice()).isEqualTo(12000L);

        // verify (optional)
        then(uriFactory).should().commodityUri(region);
        then(apiClient).should().fetch(fakeUri, CommodityListResponse.class);
        then(commodityListResponseMapper).should().toDto(region, commodityResponse);
    }

    private AuctionListResponse.AuctionResponse createAuctionResponse(Long id, Long itemId, Long quantity, Long price) {
        AuctionListResponse.AuctionResponse response = new AuctionListResponse.AuctionResponse();
        response.setId(id);
        response.setItemId(itemId);
        response.setQuantity(quantity);
        response.setBuyout(price);

        return response;
    }
}
