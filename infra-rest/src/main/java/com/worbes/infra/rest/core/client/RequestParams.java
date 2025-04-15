package com.worbes.infra.rest.core.client;

import lombok.Builder;

import java.util.Collections;
import java.util.Map;

public record RequestParams(
        String url,
        Map<String, String> queryParams,
        Map<String, String> headers,
        Object body
) {

    @Builder
    public RequestParams(String url, Map<String, String> queryParams, Map<String, String> headers, Object body) {
        this.url = url;
        this.queryParams = queryParams != null ? queryParams : Collections.emptyMap();
        this.headers = headers != null ? headers : Collections.emptyMap();
        this.body = (body != null) ? body : "";
    }
}
