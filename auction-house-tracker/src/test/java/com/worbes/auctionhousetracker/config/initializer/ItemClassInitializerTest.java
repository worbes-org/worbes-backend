package com.worbes.auctionhousetracker.config.initializer;

import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.infrastructure.rest.BlizzardApiClient;
import com.worbes.auctionhousetracker.service.ItemClassService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ItemClassInitializerTest {

    @Mock
    private ItemClassService itemClassService;

    @Mock
    private BlizzardApiClient blizzardApiClient;

    @InjectMocks
    private ItemClassInitializer itemClassInitializer;

    @DisplayName("모든 아이템 클래스가 존재하면 초기화 실행되지 않아야 한다")
    @Test
    void initialize_ShouldNotRun_WhenNoMissingItemClasses() {
        // Given
        given(itemClassService.getMissingItemClasses()).willReturn(Collections.emptySet());

        // When
        itemClassInitializer.initialize();

        // Then
        then(itemClassService).should(times(1)).getMissingItemClasses();
        then(blizzardApiClient).shouldHaveNoInteractions();
        then(itemClassService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("누락된 아이템 클래스가 있으면 Blizzard API에서 데이터를 가져와 저장해야 한다")
    @Test
    void initialize_ShouldFetchAndSave_WhenMissingItemClassesExist() {
        // Given
        Set<Long> missingItemClasses = Set.of(1L, 2L);
        ItemClassesIndexResponse mockResponse = new ItemClassesIndexResponse(Collections.emptyList());

        given(itemClassService.getMissingItemClasses()).willReturn(missingItemClasses);
        given(blizzardApiClient.fetchItemClassesIndex()).willReturn(mockResponse);

        // When
        itemClassInitializer.initialize();

        // Then
        then(itemClassService).should(times(1)).getMissingItemClasses();
        then(blizzardApiClient).should(times(1)).fetchItemClassesIndex();
        then(itemClassService).should(times(1)).saveRequiredClass(mockResponse);
    }
}
