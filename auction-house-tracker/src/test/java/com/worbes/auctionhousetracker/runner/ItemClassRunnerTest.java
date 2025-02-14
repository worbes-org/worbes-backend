package com.worbes.auctionhousetracker.runner;

import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.service.ItemClassService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.worbes.auctionhousetracker.utils.TestUtils.createDummyLanguage;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ItemClassRunnerTest {

    @Mock
    private ItemClassService itemClassService;

    @InjectMocks
    private ItemClassRunner itemClassRunner;

    @Test
    @DisplayName("이미 모든 아이템 클래스가 저장되어 있으면 추가 저장하지 않는다")
    void run_ShouldNotSave_WhenItemClassAlreadyExists() {
        // Given (이미 모든 아이템 클래스가 저장되어 있는 경우)
        given(itemClassService.count()).willReturn(ItemClassService.ITEM_CLASS_SIZE);

        // When
        itemClassRunner.run();

        // Then (fetchItemClassesIndex() 및 saveAll()이 호출되지 않아야 함)
        then(itemClassService).should(times(1)).count();
        then(itemClassService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("저장되지 않은 아이템 클래스가 있으면 API에서 데이터를 가져와 저장한다")
    void run_ShouldFetchAndSaveItemClasses_WhenNotAllExist() {
        // Given (아이템 클래스가 부족한 경우)
        given(itemClassService.count()).willReturn(0L); // 저장된 개수가 0개라고 가정

        // Mock 데이터 생성
        List<ItemClass> mockItemClasses = List.of(
                new ItemClass(1L, createDummyLanguage()),
                new ItemClass(2L, createDummyLanguage())
        );
        given(itemClassService.fetchItemClassesIndex()).willReturn(mockItemClasses);

        // When
        itemClassRunner.run();

        // Then (정상적으로 데이터가 저장되었는지 검증)
        then(itemClassService).should(times(1)).count();
        then(itemClassService).should(times(1)).fetchItemClassesIndex();
        then(itemClassService).should(times(1)).saveAll(mockItemClasses);
        then(itemClassService).shouldHaveNoMoreInteractions();
    }
}
