package com.worbes.adapter.blizzard.client;

import java.util.Optional;

public interface BlizzardAccessTokenCache {
    Optional<String> get(String s);

    void save(String s, String newToken);
}
