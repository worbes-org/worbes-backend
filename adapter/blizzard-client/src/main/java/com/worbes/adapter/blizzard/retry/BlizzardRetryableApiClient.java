package com.worbes.adapter.blizzard.retry;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
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
}
