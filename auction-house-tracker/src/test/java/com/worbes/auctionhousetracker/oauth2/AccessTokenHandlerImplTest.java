package com.worbes.auctionhousetracker.oauth2;

import com.worbes.auctionhousetracker.config.properties.RestClientConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@SpringBootTest
class AccessTokenHandlerImplTest {

    @Autowired
    AccessTokenHandler accessTokenHandler;

    @Autowired
    RestClientConfigProperties properties;

    @Autowired
    StringRedisTemplate redisTemplate;


    @Test
    void test() {
        log.info("토큰 = {}", accessTokenHandler.getToken());
    }

}
