package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ItemClassServiceTest {
    @Mock
    private ItemClassRepository itemClassRepository;

    @InjectMocks
    private ItemClassService itemClassService;

    @Test
    void testSaveItemClassWhenItemClassDoesNotExist() {
        // Given
        ItemClass itemClass = new ItemClass(0L, new Language(
                "Consumable", "Consumible", "Consumível", "Verbrauchbares", "Consumable", "Consumible",
                "Consommable", "Consumabili", "Расходуемые", "소비용품", "消耗品", "消耗品"));
        given(itemClassRepository.findById(itemClass.getId())).willReturn(Optional.empty()); // 아이템 클래스가 존재하지 않음

        // When
        itemClassService.saveItemClass(itemClass);

        // Then
        verify(itemClassRepository, times(1)).save(itemClass);  // 저장이 호출되었는지 검증
    }

    @Test
    void testSaveItemClassWhenItemClassExists() {
        // Given
        ItemClass itemClass = new ItemClass(0L, new Language(
                "Consumable", "Consumible", "Consumível", "Verbrauchbares", "Consumable", "Consumible",
                "Consommable", "Consumabili", "Расходуемые", "소비용품", "消耗品", "消耗品"));
        given(itemClassRepository.findById(itemClass.getId())).willReturn(Optional.of(itemClass)); // 아이템 클래스가 이미 존재함

        // When
        itemClassService.saveItemClass(itemClass);

        // Then
        verify(itemClassRepository, times(0)).save(itemClass);  // 저장이 호출되지 않았는지 검증
    }
}
