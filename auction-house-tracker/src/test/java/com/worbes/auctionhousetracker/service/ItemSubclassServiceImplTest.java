package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.config.properties.RequiredItemClassesProperties;
import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.repository.ItemSubclassRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ItemSubclassServiceImplTest {

    @Mock
    private ItemSubclassRepository itemSubclassRepository;

    @Mock
    private ItemClassService itemClassService;

    @Mock
    private RequiredItemClassesProperties properties;

    @InjectMocks
    private ItemSubclassServiceImpl itemSubclassService;

    @DisplayName("서브클래스를 저장하면 정상적으로 저장되어야 한다")
    @Test
    void saveRequiredSubclass_ShouldSaveSubclasses() {
        // Given
        Long itemClassId = 1L;
        ItemClass mockItemClass = ItemClass.builder().id(itemClassId).build();

        List<ItemSubclassResponse> responses = List.of(
                new ItemSubclassResponse(101L, itemClassId, Map.of("en_US", "Sword"), null),
                new ItemSubclassResponse(102L, itemClassId, Map.of("en_US", "Axe"), null)
        );

        given(itemClassService.get(itemClassId)).willReturn(mockItemClass);
        given(properties.getRequiredClasses()).willReturn(Map.of(itemClassId, Set.of(101L, 102L)));
        given(itemSubclassRepository.saveAll(anyList())).willReturn(null);

        // When
        itemSubclassService.saveRequiredSubclass(responses, itemClassId);

        // Then
        then(itemSubclassRepository).should(times(1)).saveAll(anyList());
    }

    @DisplayName("누락된 서브클래스 목록을 정확히 반환해야 한다")
    @Test
    void getMissingItemSubclasses_ShouldReturnMissingSubclasses() {
        // Given
        Long itemClassId = 1L;
        Set<Long> requiredSubclasses = Set.of(101L, 102L, 103L);

        given(properties.getRequiredClasses()).willReturn(Map.of(itemClassId, requiredSubclasses));

        List<ItemSubclass> existingSubclasses = List.of(
                ItemSubclass.builder().subclassId(101L).itemClass(ItemClass.builder().id(itemClassId).build()).build()
        );

        given(itemSubclassRepository.findByItemClassIdIn(Set.of(itemClassId)))
                .willReturn(existingSubclasses);

        // When
        Map<Long, Set<Long>> missingSubclasses = itemSubclassService.getMissingItemSubclasses();

        // Then
        assertThat(missingSubclasses).containsKey(itemClassId);
        assertThat(missingSubclasses.get(itemClassId)).containsExactlyInAnyOrder(102L, 103L);
        then(itemSubclassRepository).should(times(1)).findByItemClassIdIn(Set.of(itemClassId));
    }
}
