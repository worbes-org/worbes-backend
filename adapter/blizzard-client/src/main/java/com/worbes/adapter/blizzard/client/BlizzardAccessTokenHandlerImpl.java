package com.worbes.adapter.blizzard.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
public class BlizzardAccessTokenHandlerImpl implements BlizzardAccessTokenHandler {

    private final BlizzardConfigProperties properties;
    private final RestClient restClient;
    private final RestClientErrorHandler errorHandler;
    private final BlizzardAccessTokenCache cache;
    private final Lock lock = new ReentrantLock(true);

    public BlizzardAccessTokenHandlerImpl(
            BlizzardConfigProperties properties,
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
        try {
            if (!lock.tryLock(10L, TimeUnit.SECONDS)) {
                log.warn("토큰 갱신을 위한 락 획득 실패: 다른 스레드가 이미 수행 중일 수 있음");
                throw new RuntimeException("refresh token 갱신 락 획득 실패");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 상태 복구
            throw new RuntimeException("refresh token 갱신 대기 중 interrupted", e);
        }

        try {
            // double-check (다른 스레드가 이미 갱신했을 수 있음)
            return cache.get(properties.tokenKey())
                    .orElseGet(this::refresh);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String refresh() {
        BlizzardAccessTokenResponse accessTokenResponse = fetchNewToken();
        String newToken = accessTokenResponse.accessToken();
        cache.save(properties.tokenKey(), newToken);

        return newToken;
    }

    private BlizzardAccessTokenResponse fetchNewToken() {
        String encodedCredentials = encodeCredentials(properties.id(), properties.secret());

        return restClient.post()
                .uri(properties.tokenUrl())
                .body("grant_type=client_credentials")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handle)
                .body(BlizzardAccessTokenResponse.class);
    }

    private String encodeCredentials(String id, String secret) {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));
    }
}
