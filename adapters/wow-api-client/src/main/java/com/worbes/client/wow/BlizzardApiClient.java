package com.worbes.client.wow;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class BlizzardApiClient {

    private final RestApiClient client;
    private final BlizzardOauthClient tokenHandler;

    public <T> T fetch(String url, Map<String, String> queryParams, Class<T> responseType) {
        String token = tokenHandler.get();
        var requestParams = RequestParams.builder()
                .url(url)
                .queryParams(queryParams)
                .headers(Map.of("Authorization", "Bearer " + token))
                .build();

        return client.get(requestParams, responseType);
    }
}
