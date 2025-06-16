package com.worbes.adapter.blizzard.data.realm;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.RealmFetchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@DisplayName("RealmFetcherImpl Unit Test")
@ExtendWith(MockitoExtension.class)
class RealmFetcherImplTest {

    private final RegionType region = RegionType.KR;

    @Mock
    private BlizzardApiClient apiClient;

    @Mock
    private BlizzardApiUriFactory uriFactory;

    @Mock
    private RealmResponseMapper realmResponseMapper;

    @InjectMocks
    private RealmFetcherImpl realmFetcher;

    @Test
    @DisplayName("fetchRealmIndex는 realmIndexUri 호출 후 슬러그 목록을 반환한다")
    void shouldFetchRealmIndex() {
        URI uri = URI.create("https://api.test/realms/index");
        RealmIndexResponse response = mock(RealmIndexResponse.class);
        RealmIndexResponse.Realm realm1 = mock(RealmIndexResponse.Realm.class);
        RealmIndexResponse.Realm realm2 = mock(RealmIndexResponse.Realm.class);

        given(uriFactory.realmIndexUri(region)).willReturn(uri);
        given(apiClient.fetch(uri, RealmIndexResponse.class)).willReturn(response);
        given(response.getRealms()).willReturn(List.of(realm1, realm2));
        given(realm1.getSlug()).willReturn("slug1");
        given(realm2.getSlug()).willReturn("slug2");

        Set<String> slugs = realmFetcher.fetchRealmIndex(region);

        then(uriFactory).should().realmIndexUri(region);
        then(apiClient).should().fetch(uri, RealmIndexResponse.class);
        assertThat(slugs).containsExactlyInAnyOrder("slug1", "slug2");
    }

    @Test
    @DisplayName("fetchAllRealmsAsync는 비동기로 각 슬러그별 realm 데이터를 받아 필터링 및 매핑하여 결과 리스트를 반환한다")
    void shouldFetchAllRealmsAsync() throws Exception {
        Set<String> slugs = Set.of("slug1", "slug2");

        URI uri1 = URI.create("https://api.test/realms/slug1");
        URI uri2 = URI.create("https://api.test/realms/slug2");

        RealmResponse response1 = mock(RealmResponse.class);
        RealmResponse response2 = mock(RealmResponse.class);

        RealmFetchResult dto1 = mock(RealmFetchResult.class, "dto1");
        RealmFetchResult dto2 = mock(RealmFetchResult.class, "dto2");

        // URI factory mock
        given(uriFactory.realmUri(region, "slug1")).willReturn(uri1);
        given(uriFactory.realmUri(region, "slug2")).willReturn(uri2);

        // apiClient.fetchAsync returns completed futures
        given(apiClient.fetchAsync(uri1, RealmResponse.class)).willReturn(CompletableFuture.completedFuture(response1));
        given(apiClient.fetchAsync(uri2, RealmResponse.class)).willReturn(CompletableFuture.completedFuture(response2));

        // response1, response2 isTournament 상태 설정
        given(response1.isTournament()).willReturn(false);
        given(response2.isTournament()).willReturn(true);  // 하나는 토너먼트라 필터링 대상

        // 매핑 결과
        given(realmResponseMapper.toDto(response1, region)).willReturn(dto1);
        // response2는 필터링돼서 매핑 안 됨

        CompletableFuture<List<RealmFetchResult>> future = realmFetcher.fetchAllRealmsAsync(region, slugs);
        List<RealmFetchResult> results = future.get();  // 테스트용으로 동기화

        then(uriFactory).should().realmUri(region, "slug1");
        then(uriFactory).should().realmUri(region, "slug2");

        then(apiClient).should().fetchAsync(uri1, RealmResponse.class);
        then(apiClient).should().fetchAsync(uri2, RealmResponse.class);

        then(realmResponseMapper).should().toDto(response1, region);
        then(realmResponseMapper).should(never()).toDto(response2, region);

        assertThat(results).containsExactly(dto1);
    }
}
