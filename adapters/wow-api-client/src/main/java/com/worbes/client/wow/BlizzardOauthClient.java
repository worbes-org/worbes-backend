package com.worbes.client.wow;

import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
class BlizzardOauthClient {

    private final BlizzardApiConfigProvider provider;
    private final RestApiClient client;
    private final BlizzardAccessTokenCache cache;
    private final Object lock = new Object();

    public String get() {
        return cache.get(provider.getTokenKey())
                .orElseGet(this::refreshWithLock);
    }

    private String refreshWithLock() {
        synchronized (lock) {
            // double-check (다른 스레드가 이미 갱신했을 수 있음)
            return cache.get(provider.getTokenKey())
                    .orElseGet(this::refresh);
        }
    }

    public String refresh() {
        BlizzardTokenResponse blizzardTokenResponse = fetchNewToken();
        String newToken = blizzardTokenResponse.getAccessToken();
        long expiresIn = blizzardTokenResponse.getExpiresIn();
        cache.save(provider.getTokenKey(), newToken, expiresIn, TimeUnit.SECONDS);

        return newToken;
    }

    private BlizzardTokenResponse fetchNewToken() {
        String encodedCredentials = encodeCredentials(provider.getId(), provider.getSecret());
        var request = RequestParams.builder()
                .url(provider.getTokenUrl())
                .body(provider.getTokenBody())
                .headers(Map.of("Authorization", "Basic " + encodedCredentials))
                .build();

        return client.post(request, BlizzardTokenResponse.class);
    }

    private String encodeCredentials(String id, String secret) {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));
    }
}
