package com.worbes.adapter.blizzard.client;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface BlizzardApiClient {
    <T> T fetch(String url, Map<String, String> queryParams, Class<T> responseType);

    <T> T fetch(URI uri, Class<T> responseType);

    <T> CompletableFuture<T> fetchAsync(String url, Map<String, String> queryParams, Class<T> responseType);

    <T> CompletableFuture<T> fetchAsync(URI uri, Class<T> responseType);
}
