package com.worbes.adapter.blizzard.client;

import com.worbes.adapter.blizzard.cache.BlizzardAccessTokenCache;
import com.worbes.adapter.blizzard.client.auth.BlizzardTokenResponse;
import com.worbes.adapter.blizzard.client.core.RestApiClient;
import com.worbes.adapter.blizzard.client.core.RestApiRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlizzardAccessTokenRestClientTest {

    private final String tokenKey = "blizzard-token";
    @Mock
    private BlizzardApiProperties provider;
    @Mock
    private RestApiClient restApiClient;
    @Mock
    private BlizzardAccessTokenCache tokenCache;
    @InjectMocks
    private BlizzardAccessTokenRestClient oauthClient;

    private BlizzardTokenResponse tokenResponse;

    @BeforeEach
    void setUp() {
        given(provider.getTokenKey()).willReturn(tokenKey);
        tokenResponse = new BlizzardTokenResponse("access-token", "bearer", 1000L);
    }

    @Test
    @DisplayName("캐시에 토큰이 있으면 refresh 없이 반환한다")
    void shouldReturnCachedTokenWithoutRefresh() {
        // given
        given(tokenCache.get("blizzard-token")).willReturn(Optional.of("cached-token"));

        // when
        String result = oauthClient.get();

        // then
        assertThat(result).isEqualTo("cached-token");
        verify(tokenCache, times(1)).get(tokenKey);
        then(restApiClient).should(never()).post(any(RestApiRequest.class), eq(BlizzardTokenResponse.class));
    }

    @Test
    @DisplayName("캐시에 토큰이 없으면 refresh()를 호출하여 반환하고 저장한다")
    void shouldRefreshTokenAndSaveToCache() {
        // given
        given(tokenCache.get(tokenKey)).willReturn(Optional.empty());

        given(restApiClient.post(any(RestApiRequest.class), eq(BlizzardTokenResponse.class)))
                .willReturn(tokenResponse);

        // when
        String result = oauthClient.get();

        // then
        assertThat(result).isEqualTo("access-token");

        then(tokenCache).should().save("blizzard-token", "access-token", 1000L, TimeUnit.SECONDS);
        then(restApiClient).should().post(any(RestApiRequest.class), eq(BlizzardTokenResponse.class));
    }

    @Test
    @DisplayName("refresh()는 토큰을 가져와 저장 후 반환해야 한다")
    void shouldFetchTokenAndCacheItOnRefresh() {
        given(restApiClient.post(any(RestApiRequest.class), eq(BlizzardTokenResponse.class)))
                .willReturn(tokenResponse);

        // when
        String result = oauthClient.refresh();

        // then
        assertThat(result).isEqualTo("access-token");
        then(tokenCache).should().save("blizzard-token", "access-token", 1000L, TimeUnit.SECONDS);
    }
}
