package com.worbes.auctionhousetracker.runner;

import com.worbes.auctionhousetracker.service.ItemClassService;
import com.worbes.auctionhousetracker.service.ItemSubclassService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ItemSubclassRunnerTest {

    @Mock
    private ItemClassService itemClassService;

    @Mock
    private ItemSubclassService itemSubclassService;

    @InjectMocks
    private ItemSubclassRunner itemSubclassRunner;

    @Test
    @DisplayName("이미 모든 아이템 서브클래스가 저장되어 있으면 추가 저장하지 않는다")
    void run_ShouldNotSave_WhenItemSubclassAlreadyExists() {
        // Given (이미 모든 서브클래스가 저장된 경우)
        given(itemSubclassService.count()).willReturn(ItemSubclassService.ITEM_SUBCLASS_SIZE);

        // When
        itemSubclassRunner.run();

        // Then (아이템 서브클래스 관련 메서드가 호출되지 않아야 함)
        then(itemSubclassService).should(times(1)).count();
        then(itemClassService).shouldHaveNoMoreInteractions();
        then(itemSubclassService).shouldHaveNoMoreInteractions();
    }

}

