package com.worbes.infra.rest.common.oauth;

public interface AccessTokenHandler {

    String get();

    String refresh();
}
