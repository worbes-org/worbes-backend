package com.worbes.adapter.blizzard.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.cache.caffeine.spec=expireAfterWrite=1s"
})
@DisplayName("Integration::CaffeineAccessTokenCache::TTL")
class CaffeineAccessTokenCacheTTLTest {

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
    @DisplayName("TTL이 만료되면 Optional.empty를 반환한다")
    void returns_empty_optional_after_ttl_expires() throws InterruptedException {
        accessTokenCache.save(CACHE_KEY, TOKEN);

        // TTL 만료 대기 (1초 TTL이므로 1.5초 대기)
        Thread.sleep(1500);

        Optional<String> result = accessTokenCache.get(CACHE_KEY);

        assertThat(result).isEmpty();
    }
}
