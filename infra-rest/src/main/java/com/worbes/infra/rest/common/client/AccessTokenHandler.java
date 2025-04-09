package com.worbes.infra.rest.common.client;

public interface AccessTokenHandler {

    String get();

    String refresh();
}
