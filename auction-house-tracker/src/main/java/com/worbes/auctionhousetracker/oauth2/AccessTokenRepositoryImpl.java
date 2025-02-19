package com.worbes.auctionhousetracker.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class AccessTokenRepositoryImpl implements AccessTokenRepository {

    private final StringRedisTemplate template;

    @Override
    public String get(String key) {
        return template.opsForValue().get(key);
    }

    @Override
    public void save(String key, String value, Long expiresIn, TimeUnit timeUnit) {
        template.opsForValue().set(key, value, expiresIn, TimeUnit.SECONDS);
    }
}
