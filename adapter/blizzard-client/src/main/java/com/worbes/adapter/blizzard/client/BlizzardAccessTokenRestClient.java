package com.worbes.adapter.blizzard.client;

import com.worbes.adapter.blizzard.cache.BlizzardAccessTokenCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
public class BlizzardAccessTokenRestClient implements BlizzardAccessTokenClient {

    private final BlizzardApiProperties properties;
    private final RestClient restClient;
    private final RestClientErrorHandler errorHandler;
    private final BlizzardAccessTokenCache cache;
    private final Object lock = new Object();

    public BlizzardAccessTokenRestClient(
            BlizzardApiProperties properties,
            RestClient.Builder builder,
            RestClientErrorHandler errorHandler,
            BlizzardAccessTokenCache cache
    ) {
        this.properties = properties;
        this.restClient = builder.build();
        this.errorHandler = errorHandler;
        this.cache = cache;
    }

    @Override
    public String get() {
        return cache.get(properties.tokenKey())
                .orElseGet(this::refreshWithLock);
    }

    private String refreshWithLock() {
        synchronized (lock) {
            // double-check (다른 스레드가 이미 갱신했을 수 있음)
            return cache.get(properties.tokenKey())
                    .orElseGet(this::refresh);
        }
    }

    @Override
    public String refresh() {
        BlizzardAccessTokenResponse accessTokenResponse = fetchNewToken();
        String newToken = accessTokenResponse.accessToken();
        long expiresIn = accessTokenResponse.expiresIn();
        cache.save(properties.tokenKey(), newToken, expiresIn, TimeUnit.SECONDS);

        return newToken;
    }

    private BlizzardAccessTokenResponse fetchNewToken() {
        String encodedCredentials = encodeCredentials(properties.id(), properties.secret());

        return restClient.post()
                .uri(properties.tokenUrl())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                .body(properties.tokenBody())
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handle)
                .body(BlizzardAccessTokenResponse.class);
    }

    private String encodeCredentials(String id, String secret) {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));
    }
}
