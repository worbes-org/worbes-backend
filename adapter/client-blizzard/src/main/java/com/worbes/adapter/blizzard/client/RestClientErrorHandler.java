package com.worbes.adapter.blizzard.client;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

public interface RestClientErrorHandler {
    void handle(HttpRequest req, ClientHttpResponse res);
}
