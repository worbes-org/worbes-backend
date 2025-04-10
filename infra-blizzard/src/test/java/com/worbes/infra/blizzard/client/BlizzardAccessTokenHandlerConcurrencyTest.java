package com.worbes.infra.blizzard.client;

import com.worbes.application.core.shared.port.CacheRepository;
import com.worbes.infra.blizzard.config.BlizzardApiConfigProperties;
import com.worbes.infra.blizzard.response.TokenResponse;
import com.worbes.infra.rest.client.RestApiClient;
import com.worbes.infra.rest.factory.RestApiRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BlizzardAccessTokenHandlerConcurrencyTest {

    @Mock
    private BlizzardApiConfigProperties properties;

    @Mock
    private RestApiClient restApiClient;

    @Mock
    private CacheRepository cacheRepository;

    @InjectMocks
    private BlizzardAccessTokenHandler tokenHandler;

    @BeforeEach
    void setUp() {
        given(properties.getTokenKey()).willReturn("blizzard-token");

        AtomicBoolean tokenSaved = new AtomicBoolean(false);

        given(cacheRepository.get("blizzard-token")).willAnswer(invocation -> tokenSaved.get()
                ? Optional.of("fresh-token")
                : Optional.empty());

        willAnswer(invocation -> {
            tokenSaved.set(true);
            return null;
        }).given(cacheRepository).save(eq("blizzard-token"), eq("fresh-token"), anyLong(), eq(TimeUnit.SECONDS));

        given(restApiClient.post(any(RestApiRequest.class), eq(TokenResponse.class)))
                .willReturn(new TokenResponse("fresh-token", "bearer", 1000L));
    }

    @Test
    void shouldOnlyRefreshOnceInMultiThreadedAccess() throws InterruptedException {
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    ready.countDown(); // 쓰레드 준비 완료
                    start.await();     // 동시에 시작
                    String token = tokenHandler.get();
                    results.add(token);
                } catch (Exception e) {
                    results.add("error");
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();  // 모든 쓰레드 준비 완료
        start.countDown();  // 동시에 시작
        done.await();   // 모든 작업 완료
        executor.shutdown();

        // then
        assertThat(results)
                .hasSize(threadCount)
                .allSatisfy(token -> assertThat(token).isEqualTo("fresh-token"));

        // refresh는 정확히 한 번만 실행되어야 한다
        then(restApiClient).should(times(1)).post(any(RestApiRequest.class), eq(TokenResponse.class));
    }
}
