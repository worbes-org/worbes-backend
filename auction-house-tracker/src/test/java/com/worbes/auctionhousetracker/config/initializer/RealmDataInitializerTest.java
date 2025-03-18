package com.worbes.auctionhousetracker.config.initializer;

import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.service.RealmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealmDataInitializerTest {

    @Mock
    private RealmService realmService;

    @InjectMocks
    private RealmDataInitializer realmDataInitializer;

    @Test
    @DisplayName("서버 데이터가 이미 초기화된 경우 - 초기화 작업 스킵")
    void run_WhenDataAlreadyInitialized_ShouldSkip() {
        // given: 이미 초기화된 상태로 가정
        given(realmService.isRealmInitialized()).willReturn(true);

        // when: run() 실행
        realmDataInitializer.run();

        // then: 초기화 상태만 확인하고 추가 작업은 수행하지 않아야 함
        verify(realmService).isRealmInitialized();
        verifyNoMoreInteractions(realmService);
    }

    @Test
    @DisplayName("서버 데이터 초기화 성공")
    void run_WhenNotInitialized_ShouldInitializeAllRegions() {
        // given: 초기화되지 않은 상태로 가정
        given(realmService.isRealmInitialized()).willReturn(false);

        // 모든 Region에 대해 정상적인 비동기 작업 반환
        for (Region region : Region.values()) {
            CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
            given(realmService.fetchAndSaveRealms(region)).willReturn(future);
        }

        // when: run() 실행
        realmDataInitializer.run();

        // then: 각 Region에 대해 fetchAndSaveRealms 메서드가 호출되어야 함
        verify(realmService).isRealmInitialized();
        for (Region region : Region.values()) {
            verify(realmService).fetchAndSaveRealms(region);
        }
    }

    @Test
    @DisplayName("서버 데이터 초기화 실패 - 예외 발생")
    void run_WhenInitializationFails_ShouldThrowException() {
        // given: 초기화되지 않은 상태로 가정하고, 하나 이상의 비동기 작업에서 예외 발생
        given(realmService.isRealmInitialized()).willReturn(false);
        RuntimeException expectedException = new RuntimeException("초기화 실패");

        // 모든 Region에 대해 실패하는 CompletableFuture 반환
        given(realmService.fetchAndSaveRealms(any(Region.class)))
                .willReturn(CompletableFuture.failedFuture(expectedException));

        // when & then:
        // join() 호출 시 CompletionException이 발생하며, 그 원인은 expectedException 이어야 함.
        assertThatThrownBy(() -> realmDataInitializer.run())
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageContaining("초기화 실패");

        verify(realmService).isRealmInitialized();
        verify(realmService, atLeast(1)).fetchAndSaveRealms(any(Region.class));
    }
}
