package com.worbes.infra.blizzard.client;

public interface AccessTokenHandler {

    String get();

    String refresh();
}
