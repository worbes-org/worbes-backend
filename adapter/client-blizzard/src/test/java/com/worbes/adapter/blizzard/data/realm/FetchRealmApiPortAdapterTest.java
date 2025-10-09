package com.worbes.adapter.blizzard.data.realm;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.adapter.blizzard.data.shared.BlizzardResponseValidator;
import com.worbes.application.realm.model.Realm;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FetchRealmApiPortAdapterTest {

    @Mock
    private BlizzardApiClient apiClient;

    @Mock
    private BlizzardApiUriFactory uriFactory;

    @Mock
    private BlizzardResponseValidator validator;

    @InjectMocks
    private FetchRealmApiPortAdapter adapter;

    @Nested
    @DisplayName("fetchRealmIndex 테스트")
    class FetchRealmIndexTest {

        @Test
        @DisplayName("정상 케이스 - Slug 리스트 반환")
        void fetchRealmIndex_shouldReturnSlugSet() {
            RegionType region = RegionType.US;
            URI uri = URI.create("http://example.com/realms");
            RealmResponse r1 = new RealmResponse(1L, Map.of("en_US", "Realm1"), Map.of("href", "http://url/101"), "realm1", false);
            RealmResponse r2 = new RealmResponse(2L, Map.of("en_US", "Realm2"), Map.of("href", "http://url/102"), "realm2", false);

            RealmIndexResponse indexResponse = new RealmIndexResponse(List.of(r1, r2));

            given(uriFactory.realmIndexUri(region)).willReturn(uri);
            given(apiClient.fetch(uri, RealmIndexResponse.class)).willReturn(indexResponse);
            given(validator.validate(r1)).willReturn(r1);
            given(validator.validate(r2)).willReturn(r2);

            Set<String> result = adapter.fetchRealmIndex(region);

            assertThat(result).containsExactlyInAnyOrder("realm1", "realm2");
            then(uriFactory).should().realmIndexUri(region);
            then(apiClient).should().fetch(uri, RealmIndexResponse.class);
            then(validator).should().validate(r1);
            then(validator).should().validate(r2);
        }

        @Test
        @DisplayName("빈 리스트 반환 케이스")
        void fetchRealmIndex_emptyList_shouldReturnEmptySet() {
            RegionType region = RegionType.US;
            URI uri = URI.create("http://example.com/realms");
            RealmIndexResponse indexResponse = new RealmIndexResponse(List.of());

            given(uriFactory.realmIndexUri(region)).willReturn(uri);
            given(apiClient.fetch(uri, RealmIndexResponse.class)).willReturn(indexResponse);

            Set<String> result = adapter.fetchRealmIndex(region);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("fetchAsync 테스트")
    class FetchAsyncTest {

        @Test
        @DisplayName("정상 케이스 - CompletableFuture가 Realm 반환")
        void fetchAsync_shouldReturnRealm() throws Exception {
            RegionType region = RegionType.US;
            String slug = "test-slug";
            URI uri = URI.create("http://example.com/realms/test-slug");

            RealmResponse response = new RealmResponse(1L, Map.of("en_US", "Realm1"), Map.of("href", "http://url/101"), "realm1", false);
            Realm expectedRealm = new Realm(1L, 101L, region, Map.of("en_US", "Realm1"), "realm1");

            given(uriFactory.realmUri(region, slug)).willReturn(uri);
            given(apiClient.fetchAsync(uri, RealmResponse.class)).willReturn(CompletableFuture.completedFuture(response));
            given(validator.validate(response)).willReturn(response);

            CompletableFuture<Realm> future = adapter.fetchAsync(region, slug);
            Realm result = future.get();

            assertThat(result).isEqualTo(expectedRealm);
            then(uriFactory).should().realmUri(region, slug);
            then(apiClient).should().fetchAsync(uri, RealmResponse.class);
            then(validator).should().validate(response);
        }

        @Test
        @DisplayName("apiClient.fetchAsync 예외 케이스")
        void fetchAsync_apiException_shouldThrowException() {
            RegionType region = RegionType.US;
            String slug = "test-slug";
            URI uri = URI.create("http://example.com/realms/test-slug");

            given(uriFactory.realmUri(region, slug)).willReturn(uri);
            given(apiClient.fetchAsync(uri, RealmResponse.class)).willReturn(
                    CompletableFuture.failedFuture(new RuntimeException("unknown"))
            );

            CompletableFuture<Realm> future = adapter.fetchAsync(region, slug);

            assertThatThrownBy(future::join)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("unknown");
        }
    }
}
