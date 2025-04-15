package com.worbes.infra.rest.blizzard.client;

import com.worbes.infra.rest.blizzard.auth.AccessTokenHandler;
import com.worbes.infra.rest.core.client.RestApiClient;
import com.worbes.infra.rest.core.model.RequestParams;
import com.worbes.infra.rest.core.retry.RetryExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class BlizzardSecureClient implements BlizzardApiClient {

    private final RetryExecutor retryExecutor;
    private final RestApiClient client;
    private final AccessTokenHandler tokenHandler;

    public <T> T fetch(String url, Map<String, String> queryParams, Class<T> responseType) {
        return retryExecutor.execute(ctx -> {
            String token = tokenHandler.get();
            var requestParams = RequestParams.builder()
                    .url(url)
                    .queryParams(queryParams)
                    .headers(Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .build();

            return client.get(requestParams, responseType);
        });
    }
}
