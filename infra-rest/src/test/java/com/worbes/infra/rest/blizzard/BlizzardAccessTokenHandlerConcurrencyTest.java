package com.worbes.infra.rest.blizzard;

import com.worbes.infra.cache.TokenCache;
import com.worbes.infra.rest.core.client.RequestParams;
import com.worbes.infra.rest.core.client.RestApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class BlizzardAccessTokenHandlerConcurrencyTest {

    private static final String TOKEN_KEY = "blizzard-token";
    private static final String EXPECTED_TOKEN = "fresh-token";
    private static final BlizzardTokenResponse TOKEN_RESPONSE =
            new BlizzardTokenResponse(EXPECTED_TOKEN, "bearer", 1000L);

    @Mock
    private BlizzardApiConfigProperties properties;

    @Mock
    private RestApiClient restApiClient;

    @Mock
    private TokenCache tokenCache;

    @InjectMocks
    private BlizzardAccessTokenHandler tokenHandler;

    @BeforeEach
    void setUp() {
        given(properties.getTokenKey()).willReturn(TOKEN_KEY);

        AtomicBoolean tokenSaved = new AtomicBoolean(false);

        // 첫 번째 호출은 캐시 miss, 이후엔 hit
        given(tokenCache.get(TOKEN_KEY)).willAnswer(invocation ->
                tokenSaved.get() ? Optional.of(EXPECTED_TOKEN) : Optional.empty()
        );

        willAnswer(invocation -> {
            tokenSaved.set(true);
            return null;
        }).given(tokenCache).save(eq(TOKEN_KEY), eq(EXPECTED_TOKEN), anyLong(), eq(TimeUnit.SECONDS));

        given(restApiClient.post(any(RequestParams.class), eq(BlizzardTokenResponse.class)))
                .willReturn(TOKEN_RESPONSE);
    }

    @Test
    @DisplayName("BlizzardAccessTokenHandler가 여러 스레드에서 동시에 호출되더라도 refresh()는 정확히 1번만 호출되어야 한다.")
    void shouldOnlyRefreshOnceInMultiThreadedAccess() throws InterruptedException {
        // given
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    ready.countDown();
                    start.await();
                    results.add(tokenHandler.get());
                } catch (Exception e) {
                    results.add("error");
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();        // 모든 스레드 준비 완료
        start.countDown();    // 동시에 시작
        boolean finished = done.await(5, TimeUnit.SECONDS); // deadlock 방지 타임아웃

        executor.shutdown();

        // then
        assertThat(finished).as("모든 스레드가 시간 내에 작업을 마쳐야 함").isTrue();

        assertThat(results)
                .withFailMessage("모든 스레드가 '%s'를 반환해야 함. 실제 결과: %s", EXPECTED_TOKEN, results)
                .hasSize(threadCount)
                .allSatisfy(token -> assertThat(token).isEqualTo(EXPECTED_TOKEN));

        then(restApiClient).should(times(1))
                .post(any(RequestParams.class), eq(BlizzardTokenResponse.class));
        verifyNoMoreInteractions(restApiClient);
    }
}
