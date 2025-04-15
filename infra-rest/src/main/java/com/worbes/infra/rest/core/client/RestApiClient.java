package com.worbes.infra.rest.core.client;

import com.worbes.infra.rest.core.model.RequestParams;

public interface RestApiClient {

    <T> T get(RequestParams request, Class<T> responseType);

    <T> T post(RequestParams request, Class<T> responseType);
}
