package com.worbes.infra.rest.factory;

import lombok.Builder;

import java.util.Map;

public record GetRequestBuilder(String url, Map<String, String> queryParams, Map<String, String> headers) {
    @Builder
    public GetRequestBuilder(String url, Map<String, String> queryParams, Map<String, String> headers) {
        this.url = url;
        this.queryParams = queryParams != null ? queryParams : Map.of();
        this.headers = headers != null ? headers : Map.of();
    }
}
