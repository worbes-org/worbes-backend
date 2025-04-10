package com.worbes.infra.rest.blizzard.client;

import com.worbes.application.core.shared.port.CacheRepository;
import com.worbes.infra.rest.blizzard.config.BlizzardApiConfigProperties;
import com.worbes.infra.rest.blizzard.response.TokenResponse;
import com.worbes.infra.rest.common.client.RestApiClient;
import com.worbes.infra.rest.common.factory.PostRequestBuilder;
import com.worbes.infra.rest.common.oauth.AccessTokenHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlizzardAccessTokenHandler implements AccessTokenHandler {

    private final RestApiClient restApiClient;
    private final BlizzardApiConfigProperties properties;
    private final CacheRepository cacheRepository;

    @Override
    public String get() {
        Optional<String> token = cacheRepository.get(getTokenKey());
        if (token.isPresent()) {
            return token.get();
        }
        log.info("캐시에 블리자드 액세스 토큰 없음. 갱신 시작");
        return refresh();
    }

    @Override
    public String refresh() {
        log.info("🔄 토큰 갱신 시작");
        TokenResponse tokenResponse = fetchNewToken();
        Assert.notNull(tokenResponse, "TokenResponse must not be null");

        String newToken = tokenResponse.getAccessToken();
        long expiresIn = tokenResponse.getExpiresIn();
        cacheRepository.save(getTokenKey(), newToken, expiresIn, TimeUnit.SECONDS);

        log.info("✅ 새 토큰 갱신 완료. 유효 시간: {}초)", expiresIn);
        return newToken;
    }

    private String getTokenKey() {
        //TODO: yml에 토큰 키 설정 값 추가
        return properties.getTokenKey();
    }

    private TokenResponse fetchNewToken() {
        String credentials = getEncodedCredentials(properties.getId(), properties.getSecret());
        PostRequestBuilder<Object> request = PostRequestBuilder.builder()
                .url(properties.getTokenUrl())
                .body("grant_type=client_credentials")
                .headers(Map.of("Authorization", String.format("Basic %s", credentials)))
                .build();
        return restApiClient.post(request, TokenResponse.class);
    }

    private String getEncodedCredentials(String id, String secret) {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));
    }
}
