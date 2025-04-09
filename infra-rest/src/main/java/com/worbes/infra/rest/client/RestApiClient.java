package com.worbes.infra.rest.client;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Map;
import java.util.NoSuchElementException;

public interface RestApiClient {

    <T> T get(String path, Map<String, String> params, Class<T> responseType);

    @Retryable(
            recover = "recover",
            noRetryFor = NoSuchElementException.class,
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 5000, random = true)
    )
    <T> T get(String url, Map<String, String> params, Class<T> responseType, boolean withAuth);

    <T, R> R post(String url, Map<String, String> params, T body, Class<R> responseType);

    <T, R> R post(String url, Map<String, String> params, T body, Class<R> responseType, boolean withAuth);
}
