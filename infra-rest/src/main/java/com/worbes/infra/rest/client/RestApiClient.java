package com.worbes.infra.rest.client;

import com.worbes.infra.rest.factory.RestApiRequest;

public interface RestApiClient {

    <T> T get(RestApiRequest request, Class<T> responseType);

    <T> T post(RestApiRequest request, Class<T> responseType);
}
