package com.worbes.infra.rest.oauth;

public interface AccessTokenHandler {

    String get();

    String refresh();
}
