package com.worbes.infra.rest.factory;

import lombok.Builder;

import java.util.Map;

public record PostRequestBuilder<T>(String url, Map<String, String> queryParams, Map<String, String> headers, T body) {
    @Builder
    public PostRequestBuilder(String url, Map<String, String> queryParams, Map<String, String> headers, T body) {
        this.url = url;
        this.queryParams = queryParams != null ? queryParams : Map.of();
        this.headers = headers != null ? headers : Map.of();
        this.body = body;
    }
}
