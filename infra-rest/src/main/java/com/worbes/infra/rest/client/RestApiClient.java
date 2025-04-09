package com.worbes.infra.rest.client;

public interface RestApiClient {

    <T> T get(RestApiRequest<Void> request, Class<T> response);

    <T, R> R post(RestApiRequest<T> request, Class<R> response);
}
