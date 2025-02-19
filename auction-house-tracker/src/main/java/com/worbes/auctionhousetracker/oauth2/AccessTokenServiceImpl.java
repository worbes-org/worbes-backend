package com.worbes.auctionhousetracker.oauth2;

import com.worbes.auctionhousetracker.dto.response.TokenResponse;
import com.worbes.auctionhousetracker.repository.AccessTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AccessTokenServiceImpl implements AccessTokenService {


    private static final String TOKEN_KEY = "worbes:oauth2:token";
    private final RestClient restClient;
    private final AccessTokenRepository tokenRepository;

    public AccessTokenServiceImpl(
            @Qualifier("oauth2Client") RestClient restClient,
            AccessTokenRepository tokenRepository
    ) {
        this.tokenRepository = tokenRepository;
        this.restClient = restClient;
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
