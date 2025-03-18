package com.worbes.auctionhousetracker.config.initializer;

import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.infrastructure.rest.BlizzardApiClient;
import com.worbes.auctionhousetracker.service.ItemSubclassServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ItemSubclassInitializerTest {

    @Mock
    private ItemSubclassServiceImpl itemSubclassService;

    @Mock
    private BlizzardApiClient blizzardApiClient;

    private ItemSubclassInitializer itemSubclassInitializer;

    @BeforeEach
    void setUp() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(2);
        asyncExecutor.setQueueCapacity(10);
        asyncExecutor.initialize();

        // ✅ 수동으로 `ItemSubclassInitializer`에 `asyncExecutor` 주입
        itemSubclassInitializer = new ItemSubclassInitializer(itemSubclassService, blizzardApiClient, asyncExecutor);
    }

    @DisplayName("모든 서브클래스가 존재하면 초기화 실행되지 않아야 한다")
    @Test
    void initialize_ShouldNotRun_WhenNoMissingSubclasses() {
        // Given
        given(itemSubclassService.getMissingItemSubclasses()).willReturn(Collections.emptyMap());

        // When
        itemSubclassInitializer.initialize();

        // Then
        then(itemSubclassService).should(times(1)).getMissingItemSubclasses();
        then(blizzardApiClient).shouldHaveNoInteractions();
        then(itemSubclassService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("누락된 서브클래스가 있으면 Blizzard API에서 데이터를 가져와 저장해야 한다")
    @Test
    void initialize_ShouldFetchAndSave_WhenMissingSubclassesExist() {
        // Given
        Long itemClassId = 1L;
        Set<Long> missingSubclasses = Set.of(101L, 102L);
        Map<Long, Set<Long>> missingMap = Map.of(itemClassId, missingSubclasses);
        ItemSubclassResponse mockResponse1 = new ItemSubclassResponse(101L, itemClassId, Map.of("en_US", "Sword"), null);
        ItemSubclassResponse mockResponse2 = new ItemSubclassResponse(102L, itemClassId, Map.of("en_US", "Axe"), null);

        given(itemSubclassService.getMissingItemSubclasses()).willReturn(missingMap);
        given(blizzardApiClient.fetchItemSubclass(itemClassId, 101L)).willReturn(mockResponse1);
        given(blizzardApiClient.fetchItemSubclass(itemClassId, 102L)).willReturn(mockResponse2);

        // Mocking async execution

        // When
        itemSubclassInitializer.initialize();

        // Then
        then(itemSubclassService).should(times(1)).getMissingItemSubclasses();
        then(blizzardApiClient).should(times(1)).fetchItemSubclass(itemClassId, 101L);
        then(blizzardApiClient).should(times(1)).fetchItemSubclass(itemClassId, 102L);
        then(itemSubclassService).should(times(1)).saveRequiredSubclass(anyList(), eq(itemClassId));
    }
}
