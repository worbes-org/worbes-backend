package com.worbes.adapter.blizzard.retry;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class BlizzardRetryableApiClient implements BlizzardApiClient {

    private final RetryExecutor retryExecutor;
    private final BlizzardApiClient delegate;

    @Override
    public <T> T fetch(String url, Map<String, String> queryParams, Class<T> responseType) {
        return retryExecutor.execute(ctx ->
                delegate.fetch(url, queryParams, responseType)
        );
    }

    @Override
    public <T> CompletableFuture<T> fetchAsync(String url, Map<String, String> queryParams, Class<T> responseType) {
        return retryExecutor.execute(ctx ->
                delegate.fetchAsync(url, queryParams, responseType)
        );
    }

    @Override
    public <T> T fetch(URI uri, Class<T> responseType) {
        return retryExecutor.execute(ctx ->
                delegate.fetch(uri, responseType)
        );
    }

    @Override
    public <T> CompletableFuture<T> fetchAsync(URI uri, Class<T> responseType) {
        return retryExecutor.execute(ctx ->
                delegate.fetchAsync(uri, responseType)
        );
    }
}
