package com.worbes.adapter.blizzard.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class BlizzardApiRestClient implements BlizzardApiClient {

    private final ThreadPoolTaskExecutor executor;
    private final RestClient client;
    private final BlizzardAccessTokenHandler tokenHandler;
    private final RestClientErrorHandler errorHandler;

    public BlizzardApiRestClient(
            RestClient.Builder builder,
            BlizzardAccessTokenHandler tokenHandler,
            RestClientErrorHandler errorHandler,
            ThreadPoolTaskExecutor executor
    ) {
        this.client = builder.build();
        this.tokenHandler = tokenHandler;
        this.errorHandler = errorHandler;
        this.executor = executor;
    }

    @Override
    public <T> T fetch(URI uri, Class<T> responseType) {
        String token = tokenHandler.get();

        return client.get()
                .uri(uri)
                .accept(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handle)
                .body(responseType);
    }

    @Override
    public <T> CompletableFuture<T> fetchAsync(URI uri, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> fetch(uri, responseType), executor);
    }

    @Override
    public <T> T fetch(String url, Map<String, String> queryParams, Class<T> responseType) {
        URI uri = buildUri(url, queryParams);

        return fetch(uri, responseType);
    }

    @Override
    public <T> CompletableFuture<T> fetchAsync(String url, Map<String, String> queryParams, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> fetch(url, queryParams, responseType), executor);
    }

    private URI buildUri(String url, Map<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (!queryParams.isEmpty()) {
            queryParams.forEach(builder::queryParam);
        }
        return builder.build().toUri();
    }
}
