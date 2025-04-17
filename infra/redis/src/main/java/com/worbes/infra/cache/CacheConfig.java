package com.worbes.infra.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class CacheConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate();
    }

    @Bean
    public TokenCache tokenCache() {
        return new RedisTokenCache(stringRedisTemplate());
    }
}
