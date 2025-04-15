package com.worbes.infra.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class RedisTokenCache implements TokenCache {

    private final StringRedisTemplate template;

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(template.opsForValue().get(key));
    }

    @Override
    public void save(String key, String value, Long expiresIn, TimeUnit timeUnit) {
        template.opsForValue().set(key, value, expiresIn, timeUnit);
    }
}
