package com.worbes.auctionhousetracker.repository;

import com.worbes.auctionhousetracker.config.properties.OAuth2ConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class AccessTokenRepositoryImpl implements AccessTokenRepository{

    private final OAuth2ConfigProperties properties;
    private final StringRedisTemplate template;

    @Override
    public String get(String key) {
        return template.opsForValue().get(properties.getTokenKey());
    }

    @Override
    public void save(String key, String value, Long expiresIn, TimeUnit timeUnit) {
        template.opsForValue().set(properties.getTokenKey(), value, expiresIn, TimeUnit.SECONDS);
    }
}
