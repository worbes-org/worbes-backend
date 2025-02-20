package com.worbes.auctionhousetracker.infrastructure.oauth;

import java.util.concurrent.TimeUnit;


public interface AccessTokenRepository {
    String get(String key);

    void save(String key, String value, Long expiresIn, TimeUnit timeUnit);
}
