package com.worbes.infra.rest.core.client;

public interface RestApiClient {

    <T> T get(RequestParams request, Class<T> responseType);

    <T> T post(RequestParams request, Class<T> responseType);
}
