package com.worbes.infra.rest.common.client;

import com.worbes.infra.rest.factory.GetRequestBuilder;
import com.worbes.infra.rest.factory.PostRequestBuilder;

public interface RestApiClient {

    <T> T get(GetRequestBuilder request, Class<T> response);

    <T, R> R post(PostRequestBuilder<T> request, Class<R> response);
}
