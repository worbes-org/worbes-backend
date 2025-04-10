package com.worbes.infra.blizzard.client;

import com.worbes.application.core.shared.port.CacheRepository;
import com.worbes.infra.blizzard.config.BlizzardApiConfigProperties;
import com.worbes.infra.blizzard.response.TokenResponse;
import com.worbes.infra.rest.client.RestApiClient;
import com.worbes.infra.rest.factory.RestApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlizzardAccessTokenHandler {

    private final BlizzardApiConfigProperties properties;
    private final RestApiClient restApiClient;
    private final CacheRepository cacheRepository;

    public String get() {
        return cacheRepository.get(getTokenKey())
                .orElseGet(() -> {
                    log.info("캐시에 토큰 없음 → 새로 갱신");
                    return refresh();
                });
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
        String credentials = encodeBasicCredentials(properties.getId(), properties.getSecret());
        RestApiRequest request = RestApiRequest.builder()
                .url(properties.getTokenUrl())
                .headers(Map.of("Authorization", "Basic " + credentials))
                .body("grant_type=client_credentials")
                .build();
        return restApiClient.post(request, TokenResponse.class);
    }

    private String getTokenKey() {
        //TODO: yml에 토큰 키 설정 값 추가
        return properties.getTokenKey();
    }

    private String encodeBasicCredentials(String id, String secret) {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));
    }
}
