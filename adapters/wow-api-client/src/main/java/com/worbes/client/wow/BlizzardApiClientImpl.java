package com.worbes.client.wow;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
class BlizzardApiClientImpl implements BlizzardApiClient {

    private final RetryExecutor retryExecutor;
    private final RestApiClient client;
    private final BlizzardOauthClientImpl tokenHandler;

    public <T> T fetch(String url, Map<String, String> queryParams, Class<T> responseType) {
        return retryExecutor.execute(ctx -> {
            String token = tokenHandler.get();
            var requestParams = RequestParams.builder()
                    .url(url)
                    .queryParams(queryParams)
                    .headers(Map.of("Authorization", "Bearer " + token))
                    .build();

            return client.get(requestParams, responseType);
        });
    }
}
