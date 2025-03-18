package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.config.properties.RequiredItemClassesProperties;
import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class ItemClassServiceImplTest {

    @Mock
    private ItemClassRepository itemClassRepository;

    @Mock
    private RequiredItemClassesProperties properties;

    @InjectMocks
    private ItemClassServiceImpl itemClassService;

    @DisplayName("ID로 ItemClass를 조회하면 정상적으로 반환해야 한다")
    @Test
    void get_ShouldReturnItemClass_WhenExists() {
        // Given
        Long itemId = 1L;
        ItemClass mockItemClass = ItemClass.builder().build();
        given(itemClassRepository.findById(itemId)).willReturn(Optional.of(mockItemClass));

        // When
        ItemClass result = itemClassService.get(itemId);

        // Then
        assertThat(result).isEqualTo(mockItemClass);
        then(itemClassRepository).should(times(1)).findById(itemId);
    }

    @DisplayName("존재하지 않는 ID로 조회하면 예외를 던져야 한다")
    @Test
    void get_ShouldThrowException_WhenItemClassNotFound() {
        // Given
        Long itemId = 999L;
        given(itemClassRepository.findById(itemId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> itemClassService.get(itemId))
                .isInstanceOf(NoSuchElementException.class);
        then(itemClassRepository).should(times(1)).findById(itemId);
    }

    @DisplayName("필요한 ItemClass 목록을 저장해야 한다")
    @Test
    void saveRequiredClass_ShouldSaveNewClasses() {
        // Given
        ItemClassesIndexResponse response = new ItemClassesIndexResponse(List.of(
                new ItemClassesIndexResponse.ItemClassDto(1L, Map.of("en_US", "Weapon")),
                new ItemClassesIndexResponse.ItemClassDto(2L, Map.of("en_US", "Armor"))
        ));

        given(properties.getRequiredClasses()).willReturn(Map.of(1L, Set.of(), 2L, Set.of()));
        given(itemClassRepository.saveAll(anyList())).willReturn(null);

        // When
        itemClassService.saveRequiredClass(response);

        // Then
        then(itemClassRepository).should(times(1)).saveAll(anyList());
    }

    @DisplayName("누락된 ItemClass 목록을 반환해야 한다")
    @Test
    void getMissingItemClasses_ShouldReturnMissingClasses() {
        // Given
        Set<Long> requiredClasses = Set.of(1L, 2L, 3L);
        given(properties.getRequiredClasses()).willReturn(Map.of(1L, Set.of(), 2L, Set.of(), 3L, Set.of()));
        given(itemClassRepository.findItemClassesByIds(requiredClasses))
                .willReturn(List.of(ItemClass.builder().id(1L).build()));

        // When
        Set<Long> missingClasses = itemClassService.getMissingItemClasses();

        // Then
        assertThat(missingClasses).containsExactlyInAnyOrder(2L, 3L);
        then(itemClassRepository).should(times(1)).findItemClassesByIds(requiredClasses);
    }
}
