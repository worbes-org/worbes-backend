package com.worbes.infra.rest.blizzard.auth;


import com.worbes.application.core.shared.port.CacheRepository;
import com.worbes.infra.rest.blizzard.config.BlizzardApiConfigProperties;
import com.worbes.infra.rest.core.client.RestApiClient;
import com.worbes.infra.rest.core.model.RequestParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessTokenHandlerImpl implements AccessTokenHandler {

    private final BlizzardApiConfigProperties properties;
    private final RestApiClient client;
    private final CacheRepository cacheRepository;
    private final Object lock = new Object();

    public String get() {
        return cacheRepository.get(getTokenKey())
                .orElseGet(this::refreshWithLock);
    }

    private String refreshWithLock() {
        synchronized (lock) {
            // double-check (다른 스레드가 이미 갱신했을 수 있음)
            return cacheRepository.get(getTokenKey())
                    .orElseGet(this::refresh);
        }
    }

    public String refresh() {
        TokenResponse tokenResponse = fetchNewToken();
        String newToken = tokenResponse.getAccessToken();
        long expiresIn = tokenResponse.getExpiresIn();
        cacheRepository.save(getTokenKey(), newToken, expiresIn, TimeUnit.SECONDS);

        log.info("✅ 새 토큰 갱신 완료. 유효 시간: {}초)", expiresIn);
        return newToken;
    }

    private TokenResponse fetchNewToken() {
        String encodedCredentials = encodeCredentials(properties.getId(), properties.getSecret());
        var request = RequestParams.builder()
                .url(properties.getTokenUrl())
                .body(properties.getTokenBody())
                .headers(Map.of(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials))
                .build();

        return client.post(request, TokenResponse.class);
    }

    private String getTokenKey() {
        //TODO: yml에 토큰 키 설정 값 추가
        return properties.getTokenKey();
    }

    private String encodeCredentials(String id, String secret) {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));
    }
}
