package com.worbes.infra.blizzard.client;

import com.worbes.infra.rest.client.RestApiClient;
import com.worbes.infra.rest.factory.RestApiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class BlizzardHttpClient {

    private final RestApiClient restApiClient;
    private final BlizzardAccessTokenHandler accessTokenHandler;
    private final BlizzardRetryPolicy retryPolicy;

    public <T> T get(String url, Map<String, String> queryParams, Class<T> responseType) {
        return retryPolicy.execute(ctx -> {
            String token = accessTokenHandler.get();
            RestApiRequest request = RestApiRequest.builder()
                    .url(url)
                    .queryParams(queryParams)
                    .headers(Map.of("Authorization", "Bearer " + token))
                    .build();
            return restApiClient.get(request, responseType);
        });

    }
}
