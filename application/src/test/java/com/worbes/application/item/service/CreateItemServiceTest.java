package com.worbes.application.item.service;

import com.worbes.application.item.exception.ItemApiFetchException;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.out.FetchItemApiPort;
import com.worbes.application.item.port.out.SaveItemPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CreateItemServiceTest {

    @Mock
    private FetchItemApiPort fetchItemApiPort;

    @Mock
    private SaveItemPort saveItemPort;

    @InjectMocks
    private CreateItemService createItemService;

    @Test
    @DisplayName("아이템을 정상적으로 조회하고 저장한다")
    void execute_fetchesAndSavesItemsSuccessfully() {
        // given
        Set<Long> itemIds = Set.of(101L, 102L);

        Item item1 = Item.builder()
                .id(101L)
                .name(Map.of("ko_KR", "테스트1"))
                .classId(1L)
                .subclassId(1L)
                .quality(1)
                .level(1)
                .inventoryType("WEAPON")
                .isStackable(false)
                .icon("icon1")
                .expansionId(1)
                .build();

        Item item2 = Item.builder()
                .id(102L)
                .name(Map.of("ko_KR", "테스트2"))
                .classId(1L)
                .subclassId(1L)
                .quality(1)
                .level(1)
                .inventoryType("WEAPON")
                .isStackable(false)
                .icon("icon2")
                .expansionId(1)
                .build();

        given(fetchItemApiPort.fetchAsync(101L)).willReturn(CompletableFuture.completedFuture(item1));
        given(fetchItemApiPort.fetchAsync(102L)).willReturn(CompletableFuture.completedFuture(item2));

        // when
        createItemService.execute(itemIds);

        // then
        then(saveItemPort).should().saveAll(argThat(items ->
                items.size() == 2 &&
                        items.contains(item1) &&
                        items.contains(item2)
        ));
    }

    @Test
    @DisplayName("아이템 조회 실패 시 fallback 처리")
    void execute_itemApiFetchException_returnsNull() {
        // given
        Set<Long> itemIds = Set.of(201L);
        ItemApiFetchException ex = new ItemApiFetchException("Not Found", new RuntimeException(), 210390L);
        given(fetchItemApiPort.fetchAsync(201L)).willReturn(CompletableFuture.failedFuture(ex));

        // when
        createItemService.execute(itemIds);

        // then
        then(saveItemPort).should().saveAll(argThat(List::isEmpty));
    }

    @Test
    @DisplayName("여러 아이템 조회 중 실패한 케이스는 null로 처리되고, 저장 시 필터링된다")
    void execute_partialFailures_onlySuccessfulItemsAreSaved() {
        // given
        Set<Long> itemIds = Set.of(201L, 202L, 203L);

        Item item202 = Item.builder()
                .id(202L)
                .name(Map.of("ko_KR", "Item202"))
                .classId(1L)
                .subclassId(1L)
                .quality(1)
                .level(1)
                .inventoryType("WEAPON")
                .icon("icon.png")
                .isStackable(true)
                .expansionId(1)
                .build();

        // 201L 실패, 202L 성공, 203L 실패
        given(fetchItemApiPort.fetchAsync(201L))
                .willReturn(CompletableFuture.failedFuture(new ItemApiFetchException("Not Found", new RuntimeException(), 201L)));
        given(fetchItemApiPort.fetchAsync(202L))
                .willReturn(CompletableFuture.completedFuture(item202));
        given(fetchItemApiPort.fetchAsync(203L))
                .willReturn(CompletableFuture.failedFuture(new ItemApiFetchException("Not Found", new RuntimeException(), 203L)));

        // when
        createItemService.execute(itemIds);

        // then
        then(saveItemPort).should().saveAll(argThat(items ->
                items.size() == 1 && items.get(0).equals(item202)
        ));
    }

    @Test
    @DisplayName("알 수 없는 예외 발생 시 RuntimeException 발생")
    void execute_unknownException_throwsRuntimeException() {
        // given
        Set<Long> itemIds = Set.of(301L);
        RuntimeException ex = new RuntimeException("Unknown error");
        given(fetchItemApiPort.fetchAsync(301L)).willReturn(CompletableFuture.failedFuture(ex));

        // when & then
        assertThatThrownBy(() -> createItemService.execute(itemIds))
                .isInstanceOf(RuntimeException.class);

        then(saveItemPort).should(never()).saveAll(any());
    }
}
