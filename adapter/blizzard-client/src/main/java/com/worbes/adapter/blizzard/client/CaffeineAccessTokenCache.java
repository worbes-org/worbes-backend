package com.worbes.adapter.blizzard.client;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CaffeineAccessTokenCache implements BlizzardAccessTokenCache {

    private final Cache cache;

    public CaffeineAccessTokenCache(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("accessToken");
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(cache.get(key, String.class));
    }

    @Override
    public void save(String key, String newToken) {
        cache.put(key, newToken);
    }
}
