package com.worbes.auctionhousetracker.runner;

import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.exception.InitializationException;
import com.worbes.auctionhousetracker.service.ItemClassService;
import com.worbes.auctionhousetracker.service.ItemSubclassService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.worbes.auctionhousetracker.service.ItemClassService.ITEM_CLASS_SIZE;
import static com.worbes.auctionhousetracker.utils.TestUtils.createDummyLanguage;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
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

    @Test
    @DisplayName("저장되지 않은 서브클래스가 있으면 API에서 가져와 저장한다")
    void run_ShouldFetchAndSaveSubclasses_WhenNotAllExist() {
        // Given (저장된 서브클래스가 부족한 경우)
        given(itemSubclassService.count()).willReturn(0L);

        List<ItemClass> itemClasses = new ArrayList<>();
        for(long i = 0; i < ITEM_CLASS_SIZE; i++) {
            itemClasses.add(new ItemClass(i, createDummyLanguage()));
            given(itemSubclassService.fetchItemSubclassIds(i)).willReturn(List.of(1L, 2L, 3L));
        }

        given(itemClassService.getAll()).willReturn(itemClasses);
        given(itemSubclassService.fetchItemSubclass(any(ItemClass.class), any(Long.class)))
                .willReturn(mock(ItemSubclass.class));

        // When
        itemSubclassRunner.run();

        // Then (모든 데이터가 정상적으로 처리되는지 검증)
        then(itemSubclassService).should(times(1)).count();
        then(itemClassService).should(times(1)).getAll();
        then(itemSubclassService).should(times(1)).fetchItemSubclassIds(1L);
        then(itemSubclassService).should(times(1)).fetchItemSubclassIds(2L);
        then(itemSubclassService).should(times(itemClasses.size())).saveAll(anyList());
        then(itemSubclassService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("아이템 클래스 개수가 일치하지 않으면 예외가 발생한다")
    void run_ShouldThrowInitializationException_WhenItemClassSizeMismatch() {
        // Given (아이템 클래스 개수가 일치하지 않는 경우)
        given(itemSubclassService.count()).willReturn(0L);

        List<ItemClass> itemClasses = new ArrayList<>();
        given(itemClassService.getAll()).willReturn(itemClasses);

        // When & Then (InitializationException 예외가 발생하는지 검증)
        assertThatThrownBy(() -> itemSubclassRunner.run())
                .isInstanceOf(InitializationException.class)
                .hasMessageContaining("아이템 클래스 개수가 일치하지 않습니다.");
    }
}

