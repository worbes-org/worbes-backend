package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class ItemClassServiceImplTest {

    @Mock
    private ItemClassRepository itemClassRepository;

    @InjectMocks
    private ItemClassServiceImpl itemClassService;

    @DisplayName("Blizzard API 응답을 저장하는 테스트")
    @Test
    void save_ShouldStoreItemClasses_WhenValidResponseProvided() {
        // Given: Blizzard API에서 받은 응답 데이터
        ItemClassesIndexResponse.ItemClassDto dto1 = new ItemClassesIndexResponse.ItemClassDto(1L, Map.of("en_US", "Weapon"));
        ItemClassesIndexResponse.ItemClassDto dto2 = new ItemClassesIndexResponse.ItemClassDto(2L, Map.of("en_US", "Armor"));
        ItemClassesIndexResponse response = new ItemClassesIndexResponse(List.of(dto1, dto2));

        // When: save() 호출
        itemClassService.save(response);

        // Then: 저장된 데이터 검증
        ArgumentCaptor<List<ItemClass>> captor = ArgumentCaptor.forClass(List.class);
        then(itemClassRepository).should(times(1)).saveAll(captor.capture());
        List<ItemClass> savedItems = captor.getValue();

        assertThat(savedItems).hasSize(2);
        assertThat(savedItems.get(0).getId()).isEqualTo(1L);
        assertThat(savedItems.get(0).getName().get("en_US")).isEqualTo("Weapon");
    }

    @DisplayName("필요한 아이템 클래스가 DB에 존재하는지 확인하는 테스트")
    @Test
    void isRequiredItemClassesExist_ShouldReturnTrue_WhenAllExist() {
        // Given: DB에 존재하는 ID 설정
        given(itemClassRepository.findExistingItemClassIds(anySet()))
                .willReturn(new ArrayList<>(itemClassService.getRequiredItemClasses()));

        // When: 존재 여부 확인
        boolean result = itemClassService.isRequiredItemClassesExist();

        // Then: 모든 ID가 존재하므로 true 반환
        assertThat(result).isTrue();
        then(itemClassRepository).should(times(1)).findExistingItemClassIds(anySet());
    }

    @DisplayName("필요한 아이템 클래스가 일부 누락되었을 때 false 반환")
    @Test
    void isRequiredItemClassesExist_ShouldReturnFalse_WhenSomeMissing() {
        // Given: 일부 아이템 클래스가 누락됨
        given(itemClassRepository.findExistingItemClassIds(anySet()))
                .willReturn(List.of(0L, 1L, 3L));

        // When: 존재 여부 확인
        boolean result = itemClassService.isRequiredItemClassesExist();

        // Then: 일부 아이템이 누락되었으므로 false 반환
        assertThat(result).isFalse();
        then(itemClassRepository).should(times(1)).findExistingItemClassIds(anySet());
    }
}
