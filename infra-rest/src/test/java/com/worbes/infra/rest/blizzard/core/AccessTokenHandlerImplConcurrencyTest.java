package com.worbes.infra.rest.blizzard.core;

import com.worbes.application.core.shared.port.CacheRepository;
import com.worbes.infra.rest.blizzard.auth.AccessTokenHandlerImpl;
import com.worbes.infra.rest.blizzard.client.BlizzardApiClient;
import com.worbes.infra.rest.blizzard.config.BlizzardApiConfigProperties;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccessTokenHandlerImplConcurrencyTest {

    @Mock
    private BlizzardApiConfigProperties properties;

    @Mock
    private BlizzardApiClient blizzardApiClient;

    @Mock
    private CacheRepository cacheRepository;

    @InjectMocks
    private AccessTokenHandlerImpl tokenHandler;

//    @BeforeEach
//    void setUp() {
//        given(properties.getTokenKey()).willReturn("blizzard-token");
//
//        AtomicBoolean tokenSaved = new AtomicBoolean(false);
//
//        given(cacheRepository.get("blizzard-token")).willAnswer(invocation -> tokenSaved.get()
//                ? Optional.of("fresh-token")
//                : Optional.empty());
//
//        willAnswer(invocation -> {
//            tokenSaved.set(true);
//            return null;
//        }).given(cacheRepository).save(eq("blizzard-token"), eq("fresh-token"), anyLong(), eq(TimeUnit.SECONDS));
//
//        given(blizzardApiClient.post(any(RestApiRequestParams.class), eq(TokenResponse.class)))
//                .willReturn(new TokenResponse("fresh-token", "bearer", 1000L));
//    }
//
//    @Test
//    void shouldOnlyRefreshOnceInMultiThreadedAccess() throws InterruptedException {
//        int threadCount = 20;
//        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch ready = new CountDownLatch(threadCount);
//        CountDownLatch start = new CountDownLatch(1);
//        CountDownLatch done = new CountDownLatch(threadCount);
//        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
//
//        for (int i = 0; i < threadCount; i++) {
//            executor.submit(() -> {
//                try {
//                    ready.countDown(); // 쓰레드 준비 완료
//                    start.await();     // 동시에 시작
//                    String token = tokenHandler.get();
//                    results.add(token);
//                } catch (Exception e) {
//                    results.add("error");
//                } finally {
//                    done.countDown();
//                }
//            });
//        }
//
//        ready.await();  // 모든 쓰레드 준비 완료
//        start.countDown();  // 동시에 시작
//        done.await();   // 모든 작업 완료
//        executor.shutdown();
//
//        // then
//        assertThat(results)
//                .hasSize(threadCount)
//                .allSatisfy(token -> assertThat(token).isEqualTo("fresh-token"));
//
//        // refresh는 정확히 한 번만 실행되어야 한다
//        then(blizzardApiClient).should(times(1)).post(any(RestApiRequestParams.class), eq(TokenResponse.class));
//    }
}
