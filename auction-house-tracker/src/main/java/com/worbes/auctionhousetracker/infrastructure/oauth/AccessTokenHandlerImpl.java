package com.worbes.auctionhousetracker.infrastructure.oauth;

import com.worbes.auctionhousetracker.config.properties.BlizzardApiConfigProperties;
import com.worbes.auctionhousetracker.dto.response.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AccessTokenHandlerImpl implements AccessTokenHandler {

    private static final String TOKEN_KEY = "worbes:oauth2:token";
    private final RestClient restClient;
    private final AccessTokenRepository tokenRepository;

    public AccessTokenHandlerImpl(
            RestClient.Builder builder,
            AccessTokenRepository tokenRepository,
            BlizzardApiConfigProperties properties
    ) {
        this.tokenRepository = tokenRepository;
        String encodedCredentials = getEncodedCredentials(properties.getId(), properties.getSecret());
        this.restClient = builder
                .baseUrl(properties.getTokenUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", String.format("Basic %s", encodedCredentials))
                .build();
    }

    private String getEncodedCredentials(String id, String secret) {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get() {
        return Optional.ofNullable(tokenRepository.get(TOKEN_KEY))
                .orElseGet(this::refresh);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String refresh() {
        log.info("🔄 토큰 갱신 시작");

        TokenResponse tokenResponse = fetchNewToken();
        Assert.notNull(tokenResponse, "TokenResponse must not be null");

        String newToken = tokenResponse.getAccessToken();
        long expiresIn = tokenResponse.getExpiresIn();
        tokenRepository.save(TOKEN_KEY, newToken, expiresIn, TimeUnit.SECONDS);
        log.info("✅ 새 토큰 갱신 완료: {} (유효 시간: {}초)", newToken, expiresIn);

        return newToken;
    }

    private TokenResponse fetchNewToken() {
        return restClient.post()
                .body("grant_type=client_credentials")
                .retrieve()
                .body(TokenResponse.class);
    }
}
