package com.worbes.adapter.blizzard.data.auction;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.application.auction.port.out.FetchAuctionResult;
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
import static org.mockito.ArgumentMatchers.*;
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
    private AuctionListResponseMapper resultMapper;

    @Mock
    private BlizzardApiUriFactory uriFactory;

    @InjectMocks
    private AuctionFetcherImpl auctionFetcher;

    @Test
    @DisplayName("realmId가 존재할 때 경매장 URL을 생성하여 데이터를 fetch하고 매핑한다")
    void shouldFetchAuctionsWithRealmId() {
        Long realmId = 1234L;
        URI expectedUri = URI.create("https://some.api/auction");
        AuctionListResponse mockResponse = mock(AuctionListResponse.class);
        AuctionListResponse.AuctionResponse auction1 = createAuctionResponse(1L, 100L, 10L, 500L, 50L);
        AuctionListResponse.AuctionResponse auction2 = createAuctionResponse(2L, 200L, 20L, 1000L, 100L);

        given(uriFactory.auctionUri(region, realmId)).willReturn(expectedUri);
        given(apiClient.fetch(expectedUri, AuctionListResponse.class)).willReturn(mockResponse);
        given(mockResponse.getAuctions()).willReturn(List.of(auction1, auction2));

        FetchAuctionResult dto1 = mock(FetchAuctionResult.class, "dto1");
        FetchAuctionResult dto2 = mock(FetchAuctionResult.class, "dto2");
        given(resultMapper.toDto(region, realmId, auction1)).willReturn(dto1);
        given(resultMapper.toDto(region, realmId, auction2)).willReturn(dto2);

        // when
        List<FetchAuctionResult> results = auctionFetcher.fetch(region, realmId);

        // then
        then(uriFactory).should().auctionUri(region, realmId);
        then(apiClient).should().fetch(expectedUri, AuctionListResponse.class);
        then(resultMapper).should(times(2)).toDto(eq(region), eq(realmId), any());
        assertThat(results).containsExactly(dto1, dto2);
    }

    @Test
    @DisplayName("realmId가 null일 때 commodity URI를 생성하여 데이터를 fetch하고 매핑한다")
    void shouldFetchCommoditiesWhenRealmIdIsNull() {
        URI expectedUri = URI.create("https://some.api/commodities");
        AuctionListResponse mockResponse = mock(AuctionListResponse.class);
        AuctionListResponse.AuctionResponse auction = createAuctionResponse(3L, 300L, 30L, 3000L, 300L);

        given(uriFactory.commodityUri(region)).willReturn(expectedUri);
        given(apiClient.fetch(expectedUri, AuctionListResponse.class)).willReturn(mockResponse);
        given(mockResponse.getAuctions()).willReturn(List.of(auction));

        FetchAuctionResult dto = mock(FetchAuctionResult.class, "dto");

        given(resultMapper.toDto(eq(region), isNull(), same(auction))).willReturn(dto);

        List<FetchAuctionResult> results = auctionFetcher.fetch(region, null);

        then(uriFactory).should().commodityUri(region);
        then(apiClient).should().fetch(expectedUri, AuctionListResponse.class);
        then(resultMapper).should().toDto(region, null, auction);
        assertThat(results).containsExactly(dto);
    }

    private AuctionListResponse.AuctionResponse createAuctionResponse(long id, long itemId, long quantity, long buyout, long unitPrice) {
        AuctionListResponse.AuctionResponse response = new AuctionListResponse.AuctionResponse();
        response.setId(id);
        response.setItemId(itemId);
        response.setQuantity(quantity);
        response.setBuyout(buyout);
        response.setUnitPrice(unitPrice);
        return response;
    }
}
