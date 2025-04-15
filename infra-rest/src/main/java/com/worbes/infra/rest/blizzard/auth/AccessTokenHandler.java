package com.worbes.infra.rest.blizzard.auth;

public interface AccessTokenHandler {
    String get();

    String refresh();
}
