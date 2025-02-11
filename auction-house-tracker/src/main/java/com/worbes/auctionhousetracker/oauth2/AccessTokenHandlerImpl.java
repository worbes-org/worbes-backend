package com.worbes.auctionhousetracker.oauth2;

import com.worbes.auctionhousetracker.dto.response.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Component
public class AccessTokenHandlerImpl implements AccessTokenHandler{


    private final RestClient restClient;
    private final StringRedisTemplate redisTemplate;

    public AccessTokenHandlerImpl(@Qualifier("oauth2Client") RestClient restClient, StringRedisTemplate redisTemplate) {
        this.restClient = restClient;
        this.redisTemplate = redisTemplate;
    }

    /**
     * redis에 토큰이 없으면 API로 가져와서 redis에 저장한다.
     * 토큰이 있으면 토큰을 반환한다.
     * @return
     */
    @Override
    public String getToken() {
        String token = redisTemplate.opsForValue().get("worbes:oauth2:token");
        if(token == null) {
            log.info("Redis에 토큰 없음. 토큰 가져오기 시작");
            TokenResponse tokenResponse = Optional.ofNullable(
                    restClient.post()
                            .body("grant_type=client_credentials")
                            .retrieve()
                            .body(TokenResponse.class)
            ).orElseThrow();
            //TODO : 본문 없을 경우 예외처리
            redisTemplate.opsForValue().set("worbes:oauth2:token", tokenResponse.getAccessToken(), tokenResponse.getExpiresIn());
            return tokenResponse.getAccessToken();
        }
        log.info("Redis에 토큰 있음.");
        return token;

    }

    @Override
    public boolean isTokenValid() {
        return false;
    }
}
