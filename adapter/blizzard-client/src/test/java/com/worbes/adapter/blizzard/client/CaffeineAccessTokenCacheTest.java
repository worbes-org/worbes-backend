package com.worbes.adapter.blizzard.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Integration::CaffeineAccessTokenCache")
class CaffeineAccessTokenCacheTest {

    private final String CACHE_KEY = "test-client-id";
    private final String TOKEN = "mock-token";

    @Autowired
    private BlizzardAccessTokenCache accessTokenCache;

    @Autowired
    private CacheManager cacheManager;

    @AfterEach
    void clearCache() {
        Cache cache = cacheManager.getCache("accessToken");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    @DisplayName("get()은 캐시에 토큰이 없을 경우 빈 Optional을 반환한다")
    void get_returns_empty_optional_when_token_is_not_cached() {
        Optional<String> result = accessTokenCache.get(CACHE_KEY);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("get()은 캐시에 토큰이 저장되어 있으면 Optional로 감싸서 반환한다")
    void get_returns_token_when_cached() {
        accessTokenCache.save(CACHE_KEY, TOKEN);
        Optional<String> result = accessTokenCache.get(CACHE_KEY);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(TOKEN);
    }

    @Test
    @DisplayName("save()는 캐시에 토큰을 저장한다")
    void save_stores_token_in_cache() {
        accessTokenCache.save(CACHE_KEY, TOKEN);
        Optional<String> result = accessTokenCache.get(CACHE_KEY);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(TOKEN);
    }
}
