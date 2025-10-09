package com.worbes.adapter.blizzard.client;

import com.worbes.adapter.blizzard.BlizzardAccessTokenHandlerTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("Integration::BlizzardAccessTokenHandler")
@RestClientTest
@Import(BlizzardAccessTokenHandlerTestConfig.class)
class BlizzardAccessTokenHandlerImplTest {

    private String tokenUrl;
    private String tokenKey;
    private String encodedCredential;

    @Autowired
    private BlizzardConfigProperties configProperties;

    @Autowired
    private CaffeineAccessTokenCache tokenCache;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private BlizzardAccessTokenHandler blizzardAccessTokenHandler;

    @BeforeEach
    void setUp() {
        tokenUrl = configProperties.tokenUrl();
        tokenKey = configProperties.tokenKey();
        String id = configProperties.id();
        String secret = configProperties.secret();
        encodedCredential = Base64.getEncoder()
                .encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        Cache cache = cacheManager.getCache("accessToken");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    @DisplayName("캐시에 토큰이 있으면 refresh 없이 반환한다")
    void shouldReturnCachedTokenWithoutRefresh() {
        // Given
        tokenCache.save(tokenKey, "cached-token");

        // When
        String result = blizzardAccessTokenHandler.get();

        // Then
        assertThat(result).isEqualTo("cached-token");
        server.verify(); // RestClient 호출 없음
    }

    @Test
    @DisplayName("캐시에 토큰이 없으면 refresh()를 호출하여 반환하고 저장한다")
    void shouldRefreshTokenAndSaveToCache() {
        // Given
        server.expect(requestTo(tokenUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic " + encodedCredential))
                .andRespond(withSuccess("""
                        {
                            "access_token": "fetched-token",
                            "token_type": "bearer",
                            "expires_in": 86399
                        }
                        """, MediaType.APPLICATION_JSON));

        // When
        String result = blizzardAccessTokenHandler.get();

        // Then
        assertThat(result).isEqualTo("fetched-token");
        assertThat(tokenCache.get(tokenKey)).contains("fetched-token");
    }

    @Test
    @DisplayName("refresh()는 토큰을 가져와 저장 후 반환해야 한다")
    void shouldFetchTokenAndCacheItOnRefresh() {
        // Given
        server.expect(requestTo(tokenUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic " + encodedCredential))
                .andExpect(content().string("grant_type=client_credentials"))
                .andRespond(withSuccess("""
                        {
                            "access_token": "direct-token",
                            "token_type": "bearer",
                            "expires_in": 3600
                        }
                        """, MediaType.APPLICATION_JSON));

        // When
        String result = blizzardAccessTokenHandler.refresh();

        // Then
        assertThat(result).isEqualTo("direct-token");
        assertThat(tokenCache.get(tokenKey)).contains("direct-token");
    }
}
