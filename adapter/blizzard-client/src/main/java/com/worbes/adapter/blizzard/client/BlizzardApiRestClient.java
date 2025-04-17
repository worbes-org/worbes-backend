package com.worbes.adapter.blizzard.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class BlizzardApiRestClient implements BlizzardApiClient {

    private final RestClient client;
    private final BlizzardAccessTokenClient tokenHandler;
    private final RestClientErrorHandler errorHandler;

    public BlizzardApiRestClient(
            RestClient.Builder builder,
            BlizzardAccessTokenClient tokenHandler,
            RestClientErrorHandler errorHandler
    ) {
        this.client = builder.build();
        this.tokenHandler = tokenHandler;
        this.errorHandler = errorHandler;
    }

    @Override
    public <T> T fetch(String url, Map<String, String> queryParams, Class<T> responseType) {
        String token = tokenHandler.get();
        URI uri = buildUri(url, queryParams);

        return client.get()
                .uri(uri)
                .accept(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handle)
                .body(responseType);
    }

    private URI buildUri(String url, Map<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (!queryParams.isEmpty()) {
            queryParams.forEach(builder::queryParam);
        }
        return builder.build().toUri();
    }
}
