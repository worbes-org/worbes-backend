package com.worbes.adapter.blizzard.client;

import com.worbes.adapter.blizzard.BlizzardAccessTokenHandlerTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("Integration::BlizzardAccessTokenHandler::Concurrency")
@RestClientTest
@Import(BlizzardAccessTokenHandlerTestConfig.class)
public class BlizzardAccessTokenHandlerConcurrencyTest {

    private final String fetchedToken = "new-token";

    @Autowired
    private BlizzardConfigProperties configProperties;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private BlizzardAccessTokenHandler tokenHandler;

    @BeforeEach
    void setUp() {
        String id = configProperties.id();
        String secret = configProperties.secret();
        String encoded = Base64.getEncoder().encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));

        server.expect(once(), requestTo("https://oauth.battle.net/token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic " + encoded))
                .andExpect(content().string("grant_type=client_credentials"))
                .andRespond(withSuccess("""
                        {
                            "access_token": "%s",
                            "token_type": "bearer",
                            "expires_in": 86399
                        }
                        """.formatted(fetchedToken), MediaType.APPLICATION_JSON));

    }

    @Test
    @DisplayName("여러 스레드가 동시에 get() 호출 시 refresh는 한 번만 실행된다")
    void shouldOnlyRefreshOnceInConcurrentAccess() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<String>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> tokenHandler.get()));
        }

        List<String> tokens = new ArrayList<>();
        for (Future<String> future : futures) {
            tokens.add(future.get(3, TimeUnit.SECONDS));
        }

        executor.shutdown();

        // then
        server.verify();
        assertThat(tokens).allMatch(token -> token.equals(fetchedToken));
    }
}
