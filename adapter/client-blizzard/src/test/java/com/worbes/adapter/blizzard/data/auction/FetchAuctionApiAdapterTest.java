package com.worbes.adapter.blizzard.data.auction;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.adapter.blizzard.data.shared.BlizzardResponseValidator;
import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FetchAuctionApiAdapterTest {

    @Mock
    BlizzardApiClient apiClient;

    @Mock
    BlizzardApiUriFactory uriFactory;

    @Mock
    BlizzardResponseValidator validator;

    @InjectMocks
    FetchAuctionApiAdapter apiAdapter;

    RegionType region = RegionType.KR;
    Long realmId = 1234L;
    URI auctionUri = URI.create("http://auction");
    URI commodityUri = URI.create("http://commodity");

    @Nested
    @DisplayName("Realm 경매장 조회 (realmId != null)")
    class RealmAuctionTest {

        @Test
        @DisplayName("정상 케이스")
        void fetchAuctionApi_success() {
            AuctionItemResponse itemResponse = new AuctionItemResponse(111L, List.of(1L, 2L));

            AuctionListResponse.AuctionResponse auctionResponse = new AuctionListResponse.AuctionResponse(
                    1000L,
                    10,
                    5000L,
                    4000L,
                    itemResponse
            );

            AuctionListResponse response = new AuctionListResponse(List.of(auctionResponse));

            given(uriFactory.auctionUri(region, realmId)).willReturn(auctionUri);
            given(apiClient.fetch(auctionUri, AuctionListResponse.class)).willReturn(response);
            given(validator.validate(auctionResponse)).willReturn(auctionResponse);

            List<Auction> result = apiAdapter.fetch(region, realmId);

            assertThat(result).hasSize(1);
            Auction auction = result.get(0);
            assertThat(auction.getId()).isEqualTo(1000L);
            assertThat(auction.getItemId()).isEqualTo(111L);
            assertThat(auction.getItemBonus()).containsExactly(1L, 2L);
            assertThat(auction.getPrice()).isEqualTo(5000L);
            assertThat(auction.getQuantity()).isEqualTo(10);
            assertThat(auction.getRegion()).isEqualTo(region);
            assertThat(auction.getRealmId()).isEqualTo(realmId);

            then(uriFactory).should().auctionUri(region, realmId);
            then(apiClient).should().fetch(auctionUri, AuctionListResponse.class);
            then(validator).should().validate(auctionResponse);
        }

        @Test
        @DisplayName("경계 케이스 - 빈 리스트")
        void fetchAuctionApi_empty() {
            AuctionListResponse response = new AuctionListResponse(List.of());

            given(uriFactory.auctionUri(region, realmId)).willReturn(auctionUri);
            given(apiClient.fetch(auctionUri, AuctionListResponse.class)).willReturn(response);

            List<Auction> result = apiAdapter.fetch(region, realmId);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("예외 케이스 - apiClient.fetch 예외 전파")
        void fetchAuctionApi_exception() {
            given(uriFactory.auctionUri(region, realmId)).willReturn(auctionUri);
            given(apiClient.fetch(auctionUri, AuctionListResponse.class))
                    .willThrow(new RuntimeException("API error"));

            assertThatThrownBy(() -> apiAdapter.fetch(region, realmId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("API error");
        }
    }

    @Nested
    @DisplayName("Commodity 경매장 조회 (realmId == null)")
    class CommodityAuctionTest {

        @Test
        @DisplayName("정상 케이스")
        void fetchCommodityApi_success() {
            AuctionItemResponse itemResponse = new AuctionItemResponse(222L, null);

            CommodityListResponse.CommodityResponse commodityResponse = new CommodityListResponse.CommodityResponse(
                    2000L,
                    30,
                    1500L,
                    itemResponse
            );

            CommodityListResponse response = new CommodityListResponse(List.of(commodityResponse));

            given(uriFactory.commodityUri(region)).willReturn(commodityUri);
            given(apiClient.fetch(commodityUri, CommodityListResponse.class)).willReturn(response);
            given(validator.validate(commodityResponse)).willReturn(commodityResponse);

            List<Auction> result = apiAdapter.fetch(region, null);

            assertThat(result).hasSize(1);
            Auction auction = result.get(0);
            assertThat(auction.getId()).isEqualTo(2000L);
            assertThat(auction.getItemId()).isEqualTo(222L);
            assertThat(auction.getPrice()).isEqualTo(1500L);
            assertThat(auction.getQuantity()).isEqualTo(30);
            assertThat(auction.getRegion()).isEqualTo(region);
            assertThat(auction.getRealmId()).isNull();

            then(uriFactory).should().commodityUri(region);
            then(apiClient).should().fetch(commodityUri, CommodityListResponse.class);
            then(validator).should().validate(commodityResponse);
        }

        @Test
        @DisplayName("경계 케이스 - 빈 리스트")
        void fetchCommodityApi_empty() {
            CommodityListResponse response = new CommodityListResponse(List.of());

            given(uriFactory.commodityUri(region)).willReturn(commodityUri);
            given(apiClient.fetch(commodityUri, CommodityListResponse.class)).willReturn(response);

            List<Auction> result = apiAdapter.fetch(region, null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("예외 케이스 - apiClient.fetch 예외 전파")
        void fetchCommodityApi_exception() {
            given(uriFactory.commodityUri(region)).willReturn(commodityUri);
            given(apiClient.fetch(commodityUri, CommodityListResponse.class))
                    .willThrow(new RuntimeException("API error"));

            assertThatThrownBy(() -> apiAdapter.fetch(region, null))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("API error");
        }
    }
}
