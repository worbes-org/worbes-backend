package com.worbes.infra.rest.blizzard;


import com.worbes.infra.cache.TokenCache;
import com.worbes.infra.rest.core.client.RequestParams;
import com.worbes.infra.rest.core.client.RestApiClient;
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
class BlizzardAccessTokenHandler {

    private final BlizzardApiConfigProperties properties;
    private final RestApiClient client;
    private final TokenCache cache;
    private final Object lock = new Object();

    public String get() {
        return cache.get(getTokenKey())
                .orElseGet(this::refreshWithLock);
    }

    private String refreshWithLock() {
        synchronized (lock) {
            // double-check (다른 스레드가 이미 갱신했을 수 있음)
            return cache.get(getTokenKey())
                    .orElseGet(this::refresh);
        }
    }

    public String refresh() {
        BlizzardTokenResponse blizzardTokenResponse = fetchNewToken();
        String newToken = blizzardTokenResponse.getAccessToken();
        long expiresIn = blizzardTokenResponse.getExpiresIn();
        cache.save(getTokenKey(), newToken, expiresIn, TimeUnit.SECONDS);

        log.info("✅ 새 토큰 갱신 완료. 유효 시간: {}초)", expiresIn);
        return newToken;
    }

    private BlizzardTokenResponse fetchNewToken() {
        String encodedCredentials = encodeCredentials(properties.getId(), properties.getSecret());
        var request = RequestParams.builder()
                .url(properties.getTokenUrl())
                .body(properties.getTokenBody())
                .headers(Map.of(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials))
                .build();

        return client.post(request, BlizzardTokenResponse.class);
    }

    private String getTokenKey() {
        //TODO: yml에 토큰 키 설정 값 추가
        return properties.getTokenKey();
    }

    private String encodeCredentials(String id, String secret) {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));
    }
}
