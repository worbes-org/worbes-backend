package com.worbes.infra.cache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface TokenCache {
    Optional<String> get(String key);

    void save(String key, String value, Long expiresIn, TimeUnit timeUnit);
}
