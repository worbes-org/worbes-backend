package com.worbes.infra.rest.client;

public interface AccessTokenHandler {

    String get();

    String refresh();
}
