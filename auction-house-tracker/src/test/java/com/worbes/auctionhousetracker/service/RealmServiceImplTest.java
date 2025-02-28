package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.dto.response.RealmResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.exception.RestApiClientException;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import com.worbes.auctionhousetracker.repository.RealmRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static com.worbes.auctionhousetracker.TestUtils.loadJsonResource;
import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.DYNAMIC;
import static com.worbes.auctionhousetracker.service.RealmServiceImpl.extractIdFromUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RealmServiceImplTest {

    @Mock
    RealmRepository realmRepository;

    @Mock
    RestApiClient restApiClient;

    @InjectMocks
    RealmServiceImpl realmService;

    @Test
    @DisplayName("서버 목록 조회 - 성공")
    void fetchRealmIndexSuccess() {
        // given
        Region region = Region.KR;
        RealmIndexResponse realmIndexResponse = loadJsonResource("/json/realm-index-response.json", RealmIndexResponse.class);
        String path = BlizzardApiUrlBuilder.builder(region).realmIndex().build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build();

        given(restApiClient.get(eq(path), eq(params), eq(RealmIndexResponse.class)))
                .willReturn(realmIndexResponse);

        // when
        RealmIndexResponse response = realmService.fetchRealmIndex(region);

        // then
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(realmIndexResponse);

        // verify API call
        verify(restApiClient).get(eq(path), eq(params), eq(RealmIndexResponse.class));
    }

    @Test
    @DisplayName("서버 목록 조회 - API 실패")
    void fetchRealmIndexWhenApiFails() {
        // given
        Region region = Region.KR;
        String path = BlizzardApiUrlBuilder.builder(region).realmIndex().build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build();

        given(restApiClient.get(eq(path), eq(params), eq(RealmIndexResponse.class)))
                .willThrow(new RestApiClientException("API 오류"));

        // when & then
        assertThatThrownBy(() -> realmService.fetchRealmIndex(region))
                .isInstanceOf(RestApiClientException.class)
                .hasMessage("API 오류");
    }

    @Test
    @DisplayName("서버 정보 조회 - 성공")
    void fetchRealmSuccess() {
        // given
        Region region = Region.KR;
        String slug = "hyjal";
        RealmResponse realmResponse = loadJsonResource("/json/realm-response.json", RealmResponse.class);
        String path = BlizzardApiUrlBuilder.builder(region).realm(slug).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build();

        given(restApiClient.get(eq(path), eq(params), eq(RealmResponse.class)))
                .willReturn(realmResponse);

        // when
        Realm realm = realmService.fetchRealm(region, slug);

        // then
        assertThat(realm).isNotNull();
        assertThat(realm.getId()).isEqualTo(realmResponse.getId());
        assertThat(realm.getName()).isEqualTo(realmResponse.getName());
        assertThat(realm.getConnectedRealmId()).isEqualTo(2116L); // connected realm href에서 추출된 ID

        // verify API call
        verify(restApiClient).get(eq(path), eq(params), eq(RealmResponse.class));
    }

    @Test
    @DisplayName("서버 정보 조회 - API 실패")
    void fetchRealmWhenApiFails() {
        // given
        Region region = Region.KR;
        String slug = "hyjal";
        String path = BlizzardApiUrlBuilder.builder(region).realm(slug).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build();

        given(restApiClient.get(eq(path), eq(params), eq(RealmResponse.class)))
                .willThrow(new RestApiClientException("API 오류"));

        // when & then
        assertThatThrownBy(() -> realmService.fetchRealm(region, slug))
                .isInstanceOf(RestApiClientException.class)
                .hasMessage("API 오류");
    }

    @Test
    @DisplayName("URL에서 ID 추출 - 성공")
    void extractIdFromUrlSuccess() {
        // given
        String url = "https://kr.api.blizzard.com/data/wow/connected-realm/2116?namespace=dynamic-kr";

        // when
        Long id = extractIdFromUrl(url);

        // then
        assertThat(id).isEqualTo(2116L);
    }

    @Test
    @DisplayName("URL에서 ID 추출 - 잘못된 형식의 URL")
    void extractIdFromUrlWithInvalidFormat() {
        // given
        String invalidUrl = "https://kr.api.blizzard.com/data/wow/connected-realm/invalid?namespace=dynamic-kr";

        // when & then
        assertThatThrownBy(() -> extractIdFromUrl(invalidUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid ID format in URL: " + invalidUrl);
    }

    @Test
    @DisplayName("URL에서 ID 추출 - null URL")
    void extractIdFromUrlWithNull() {
        // when & then
        assertThatThrownBy(() -> extractIdFromUrl(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL must not be null or empty");
    }

    @Test
    @DisplayName("URL에서 ID 추출 - 빈 URL")
    void extractIdFromUrlWithEmptyString() {
        // when & then
        assertThatThrownBy(() -> extractIdFromUrl(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL must not be null or empty");
    }
}
