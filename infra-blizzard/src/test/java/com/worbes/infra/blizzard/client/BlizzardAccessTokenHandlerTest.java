package com.worbes.infra.blizzard.client;

import com.worbes.application.core.shared.port.CacheRepository;
import com.worbes.infra.blizzard.config.BlizzardApiConfigProperties;
import com.worbes.infra.blizzard.response.TokenResponse;
import com.worbes.infra.rest.client.RestApiClient;
import com.worbes.infra.rest.factory.RestApiRequest;
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
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BlizzardAccessTokenHandlerTest {

    @Mock
    private BlizzardApiConfigProperties properties;

    @Mock
    private RestApiClient restApiClient;

    @Mock
    private CacheRepository cacheRepository;

    @InjectMocks
    private BlizzardAccessTokenHandler tokenHandler;

    @BeforeEach
    void setUp() {
        given(properties.getTokenKey()).willReturn("blizzard-token");
    }

    @Test
    @DisplayName("캐시에 토큰이 있으면 refresh 없이 반환한다")
    void shouldReturnCachedTokenWithoutRefresh() {
        // given
        given(cacheRepository.get("blizzard-token")).willReturn(Optional.of("cached-token"));

        // when
        String result = tokenHandler.get();

        // then
        assertThat(result).isEqualTo("cached-token");
        then(restApiClient).should(never()).post(any(), eq(TokenResponse.class));
    }

    @Test
    @DisplayName("캐시에 토큰이 없으면 refresh()를 호출하여 반환하고 저장한다")
    void shouldRefreshTokenAndSaveToCache() {
        // given
        given(cacheRepository.get("blizzard-token")).willReturn(Optional.empty());

        TokenResponse tokenResponse = new TokenResponse("access-token", "bearer", 1000L);
        given(restApiClient.post(any(RestApiRequest.class), eq(TokenResponse.class)))
                .willReturn(tokenResponse);

        // when
        String result = tokenHandler.get();

        // then
        assertThat(result).isEqualTo("access-token");

        then(cacheRepository).should().save("blizzard-token", "access-token", 1000L, TimeUnit.SECONDS);
        then(restApiClient).should().post(any(RestApiRequest.class), eq(TokenResponse.class));
    }

    @Test
    @DisplayName("refresh()는 토큰을 가져와 저장 후 반환해야 한다")
    void shouldFetchTokenAndCacheItOnRefresh() {
        TokenResponse tokenResponse = new TokenResponse("access-token", "bearer", 1000L);
        given(restApiClient.post(any(RestApiRequest.class), eq(TokenResponse.class)))
                .willReturn(tokenResponse);

        // when
        String result = tokenHandler.refresh();

        // then
        assertThat(result).isEqualTo("access-token");
        then(cacheRepository).should().save("blizzard-token", "access-token", 1000L, TimeUnit.SECONDS);
    }
}
