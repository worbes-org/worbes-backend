package com.worbes.client.wow;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface BlizzardAccessTokenCache {
    Optional<String> get(String key);

    void save(String key, String value, Long expiresIn, TimeUnit timeUnit);
}
