package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.client.BlizzardRestClient;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import com.worbes.auctionhousetracker.exception.BlizzardApiException;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemClassServiceTest {
    @Mock
    private ItemClassRepository itemClassRepository;

    @Mock
    private BlizzardRestClient restClient;

    @InjectMocks
    private ItemClassService itemClassService;

    @Test
    void init_Success_WhenItemClassesNotSaved() {
        // Given
        given(itemClassRepository.count()).willReturn(0L);
        List<ItemClass> itemClasses = List.of(
                new ItemClass(17L, new Language(
                        "Battle Pets", "Mascotas de duelo", "Mascotes de Batalha", "Kampfhaustiere",
                        "Battle Pets", "Mascotas de duelo", "Mascottes de combat", "Mascotte",
                        "Боевые питомцы", "전투 애완동물", "戰寵", "战斗宠物"
                )),
                new ItemClass(0L, new Language(
                        "Consumable", "Consumible", "Consumível", "Verbrauchbares",
                        "Consumable", "Consumible", "Consommable", "Consumabili",
                        "Расходуемые", "소비용품", "消耗品", "消耗品"
                ))
        );
        given(restClient.fetchItemClassesIndex()).willReturn(itemClasses);

        // When
        itemClassService.init();

        // Then
        verify(itemClassRepository, times(1)).saveAll(itemClasses);
        verify(itemClassRepository, times(1)).count();
        verify(restClient, times(1)).fetchItemClassesIndex();
    }

    @Test
    void init_Skip_WhenAllItemClassesAlreadySaved() {
        // Given
        given(itemClassRepository.count()).willReturn((long) ItemClassService.ITEM_CLASS_SIZE);

        // When
        itemClassService.init();

        // Then
        verify(itemClassRepository, times(1)).count();
        verify(itemClassRepository, never()).saveAll(anyList());
        verify(restClient, never()).fetchItemClassesIndex();
    }

    @Test
    public void init_Fail_WhenItemClassesIsEmptyAndApiCall() {
        given(itemClassRepository.findAll()).willReturn(new ArrayList<>());
        given(restClient.fetchItemClassesIndex()).willThrow(new BlizzardApiException("API 오류"));

        assertThatThrownBy(() -> itemClassService.init());

        verify(itemClassRepository, times(0)).saveAll(anyList());
    }
}
