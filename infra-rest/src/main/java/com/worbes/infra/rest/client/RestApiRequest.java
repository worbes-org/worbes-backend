package com.worbes.infra.rest.client;

import lombok.Builder;

import java.util.Map;

public record RestApiRequest<T>(String url, Map<String, String> queryParams, Map<String, String> headers, T body) {
    @Builder
    public RestApiRequest(String url, Map<String, String> queryParams, Map<String, String> headers, T body) {
        this.url = url;
        this.queryParams = queryParams != null ? queryParams : Map.of();
        this.headers = headers != null ? headers : Map.of();
        this.body = body;
    }
}
