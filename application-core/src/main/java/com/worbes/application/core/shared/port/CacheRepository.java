package com.worbes.application.core.shared.port;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface CacheRepository {
    Optional<String> get(String key);

    void save(String key, String token, Long expiresIn, TimeUnit timeUnit);
}
