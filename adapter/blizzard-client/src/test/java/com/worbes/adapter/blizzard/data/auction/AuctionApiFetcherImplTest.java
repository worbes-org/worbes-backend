package com.worbes.adapter.blizzard.data.auction;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.adapter.blizzard.data.shared.BlizzardResponseValidator;
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
import com.worbes.application.auction.model.Auction;

@ExtendWith(MockitoExtension.class)
class AuctionApiFetcherImplTest {

    private final RegionType region = RegionType.KR;

    @Mock
    private BlizzardApiClient apiClient;

    @Mock
    private AuctionListResponseMapper auctionListResponseMapper;

    @Mock
    private CommodityListResponseMapper commodityListResponseMapper;

    @Mock
    private BlizzardResponseValidator blizzardResponseValidator;

    @Mock
    private BlizzardApiUriFactory uriFactory;

    @InjectMocks
    private AuctionApiFetcherImpl auctionFetcher;

    @Test
    @DisplayName("경매장 URL을 생성하여 데이터를 fetch하고 매핑한다")
    void shouldFetchAuctionsWithRealmId() {
        Long realmId = 1234L;
        URI expectedUri = URI.create("https://some.api/auction");
        AuctionListResponse mockResponse = mock(AuctionListResponse.class);
        AuctionListResponse.AuctionResponse auction1 = createAuctionResponse(1L, 100L, 10, 500L);
        AuctionListResponse.AuctionResponse auction2 = createAuctionResponse(2L, 200L, 20, 1000L);

        given(uriFactory.auctionUri(region, realmId)).willReturn(expectedUri);
        given(apiClient.fetch(expectedUri, AuctionListResponse.class)).willReturn(mockResponse);
        given(mockResponse.getAuctions()).willReturn(List.of(auction1, auction2));
        given(blizzardResponseValidator.validate(auction1)).willReturn(auction1);
        given(blizzardResponseValidator.validate(auction2)).willReturn(auction2);

        Auction auctionDto1 = mock(Auction.class, "auctionDto1");
        Auction auctionDto2 = mock(Auction.class, "auctionDto2");
        given(auctionListResponseMapper.toDomain(region, realmId, auction1)).willReturn(auctionDto1);
        given(auctionListResponseMapper.toDomain(region, realmId, auction2)).willReturn(auctionDto2);

        // when
        List<Auction> results = auctionFetcher.fetchAuctions(region, realmId);

        // then
        then(uriFactory).should().auctionUri(region, realmId);
        then(apiClient).should().fetch(expectedUri, AuctionListResponse.class);
        then(auctionListResponseMapper).should(times(2)).toDomain(eq(region), eq(realmId), any());
        assertThat(results).containsExactly(auctionDto1, auctionDto2);
    }

    @Test
    @DisplayName("RegionType에 따른 CommodityList를 FetchCommodityResult 리스트로 변환한다")
    void shouldFetchAndMapCommodities() {
        // given
        RegionType region = RegionType.KR;
        URI fakeUri = URI.create("https://test.com/commodity");
        Long id = 1001L;
        Long itemId = 5555L;
        Integer quantity = 30;
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

        given(blizzardResponseValidator.validate(commodityResponse)).willReturn(commodityResponse);

        Auction expectedResult = mock(Auction.class);
        given(commodityListResponseMapper.toDomain(region, commodityResponse)).willReturn(expectedResult);

        // when
        List<Auction> results = auctionFetcher.fetchCommodities(region);

        // then
        assertThat(results).hasSize(1);
        Auction result = results.get(0);
        // 추가적으로 Auction의 필드에 대한 검증이 필요하다면, mock 설정 및 검증을 추가하세요.

        // verify (optional)
        then(uriFactory).should().commodityUri(region);
        then(apiClient).should().fetch(fakeUri, CommodityListResponse.class);
        then(commodityListResponseMapper).should().toDomain(region, commodityResponse);
    }

    private AuctionListResponse.AuctionResponse createAuctionResponse(Long id, Long itemId, Integer quantity, Long price) {
        AuctionListResponse.AuctionResponse response = new AuctionListResponse.AuctionResponse();
        response.setId(id);
        response.setItemId(itemId);
        response.setQuantity(quantity);
        response.setBuyout(price);

        return response;
    }
}
