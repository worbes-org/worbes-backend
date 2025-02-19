package com.worbes.auctionhousetracker.oauth2;

import java.util.concurrent.TimeUnit;


public interface AccessTokenRepository {
    String get(String key);

    void save(String key, String value, Long expiresIn, TimeUnit timeUnit);
}
