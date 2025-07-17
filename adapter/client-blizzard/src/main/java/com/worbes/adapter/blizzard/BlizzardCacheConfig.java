package com.worbes.adapter.blizzard;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class BlizzardCacheConfig {

    @Bean
    public CacheManager cacheManager(CacheProperties cacheProperties) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("accessToken");
        cacheManager.setCacheSpecification(cacheProperties.getCaffeine().getSpec());
        return cacheManager;
    }
}
