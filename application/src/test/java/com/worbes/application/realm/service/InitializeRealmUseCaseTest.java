package com.worbes.application.realm.service;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.FetchRealmApiPort;
import com.worbes.application.realm.port.out.FindRealmPort;
import com.worbes.application.realm.port.out.SaveRealmPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class InitializeRealmUseCaseTest {

    @Mock
    private SaveRealmPort saveRealmPort;

    @Mock
    private FindRealmPort findRealmPort;

    @Mock
    private FetchRealmApiPort fetchRealmApiPort;

    @InjectMocks
    private InitializeRealmService initializeRealmUseCase;

    @Test
    @DisplayName("(정상) 누락된 Realm이 있을 때 정상적으로 저장 및 반환한다.")
    void execute_shouldSaveAndReturnRealms_whenMissingSlugsExist() {
        // given
        RegionType region = RegionType.KR;
        Set<String> fetchedSlugs = Set.of("slug1", "slug2");
        Set<String> existingSlugs = Set.of("slug1");
        Realm realm = new Realm(1L, 100L, region, Map.of("ko_KR", "테스트"), "slug2");
        List<Realm> fetchedRealms = List.of(realm);
        List<Realm> savedRealms = List.of(realm);

        given(fetchRealmApiPort.fetchRealmIndex(region)).willReturn(fetchedSlugs);
        given(findRealmPort.findSlugByRegion(region)).willReturn(existingSlugs);
        given(fetchRealmApiPort.fetchAsync(region, "slug2")).willReturn(CompletableFuture.completedFuture(realm));
        given(saveRealmPort.saveAll(new HashSet<>(fetchedRealms))).willReturn(savedRealms);

        // when
        List<Realm> result = initializeRealmUseCase.execute(region);

        // then
        assertThat(result).containsExactlyElementsOf(savedRealms);
    }

    @Test
    @DisplayName("(경계) 누락된 Realm이 없을 때 빈 리스트를 반환한다.")
    void execute_shouldReturnEmptyList_whenNoMissingSlugs() {
        // given
        RegionType region = RegionType.KR;
        Set<String> fetchedSlugs = Set.of("slug1");
        Set<String> existingSlugs = Set.of("slug1");
        given(fetchRealmApiPort.fetchRealmIndex(region)).willReturn(fetchedSlugs);
        given(findRealmPort.findSlugByRegion(region)).willReturn(existingSlugs);

        // when
        List<Realm> result = initializeRealmUseCase.execute(region);

        // then
        assertThat(result).isEmpty();
        verifyNoInteractions(saveRealmPort);
    }

    @Test
    @DisplayName("(실패) fetchAllRealmsAsync에서 예외 발생 시 RuntimeException이 발생한다.")
    void execute_shouldThrowException_whenFetchAllRealmsAsyncFails() {
        // given
        RegionType region = RegionType.KR;
        Set<String> fetchedSlugs = Set.of("slug1", "slug2");
        Set<String> existingSlugs = Set.of("slug1");
        Set<String> missingSlugs = Set.of("slug2");
        given(fetchRealmApiPort.fetchRealmIndex(region)).willReturn(fetchedSlugs);
        given(findRealmPort.findSlugByRegion(region)).willReturn(existingSlugs);
        given(fetchRealmApiPort.fetchAsync(region, "slug2")).willReturn(CompletableFuture.failedFuture(new RuntimeException("API 실패")));

        // when & then
        assertThrows(RuntimeException.class, () -> {
            initializeRealmUseCase.execute(region);
        });
    }
} 
