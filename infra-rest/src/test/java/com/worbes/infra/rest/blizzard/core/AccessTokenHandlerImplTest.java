package com.worbes.infra.rest.blizzard.core;

import com.worbes.application.core.shared.port.CacheRepository;
import com.worbes.infra.rest.blizzard.auth.AccessTokenHandlerImpl;
import com.worbes.infra.rest.blizzard.auth.TokenResponse;
import com.worbes.infra.rest.blizzard.client.BlizzardApiClient;
import com.worbes.infra.rest.blizzard.config.BlizzardApiConfigProperties;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccessTokenHandlerImplTest {

    @Mock
    private BlizzardApiConfigProperties properties;

    @Mock
    private BlizzardApiClient blizzardApiClient;

    @Mock
    private CacheRepository cacheRepository;

    @InjectMocks
    private AccessTokenHandlerImpl tokenHandler;

    private TokenResponse tokenResponse;

//    @BeforeEach
//    void setUp() {
//        given(properties.getTokenKey()).willReturn("blizzard-token");
//        tokenResponse = new TokenResponse("access-token", "bearer", 1000L);
//    }
//
//    @Test
//    @DisplayName("캐시에 토큰이 있으면 refresh 없이 반환한다")
//    void shouldReturnCachedTokenWithoutRefresh() {
//        // given
//        given(cacheRepository.get("blizzard-token")).willReturn(Optional.of("cached-token"));
//
//        // when
//        String result = tokenHandler.get();
//
//        // then
//        assertThat(result).isEqualTo("cached-token");
//        then(blizzardApiClient).should(never()).post(any(), eq(TokenResponse.class));
//    }
//
//    @Test
//    @DisplayName("캐시에 토큰이 없으면 refresh()를 호출하여 반환하고 저장한다")
//    void shouldRefreshTokenAndSaveToCache() {
//        // given
//        given(cacheRepository.get("blizzard-token")).willReturn(Optional.empty());
//
//        given(blizzardApiClient.post(any(RestApiRequestParams.class), eq(TokenResponse.class)))
//                .willReturn(tokenResponse);
//
//        // when
//        String result = tokenHandler.get();
//
//        // then
//        assertThat(result).isEqualTo("access-token");
//
//        then(cacheRepository).should().save("blizzard-token", "access-token", 1000L, TimeUnit.SECONDS);
//        then(blizzardApiClient).should().post(any(RestApiRequestParams.class), eq(TokenResponse.class));
//    }
//
//    @Test
//    @DisplayName("refresh()는 토큰을 가져와 저장 후 반환해야 한다")
//    void shouldFetchTokenAndCacheItOnRefresh() {
//        given(blizzardApiClient.post(any(RestApiRequestParams.class), eq(TokenResponse.class)))
//                .willReturn(tokenResponse);
//
//        // when
//        String result = tokenHandler.refresh();
//
//        // then
//        assertThat(result).isEqualTo("access-token");
//        then(cacheRepository).should().save("blizzard-token", "access-token", 1000L, TimeUnit.SECONDS);
//    }
}
