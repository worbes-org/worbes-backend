package com.worbes.infra.rest.core.client;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

public interface RestApiErrorHandler {
    void handle(HttpRequest req, ClientHttpResponse res);
}
