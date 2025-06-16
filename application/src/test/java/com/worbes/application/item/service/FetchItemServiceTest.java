package com.worbes.application.item.service;

import com.worbes.application.item.exception.ItemFetchException;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.out.ItemFetchResult;
import com.worbes.application.item.port.out.ItemFetcher;
import com.worbes.application.item.port.out.MediaFetchResult;
import com.worbes.application.item.port.out.MediaFetcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class FetchItemServiceTest {

    @Mock
    private ItemFetcher itemFetcher;

    @Mock
    private MediaFetcher mediaFetcher;

    @Mock
    private ItemFactory itemFactory;

    @InjectMocks
    private FetchItemService fetchItemService;

    @Test
    @DisplayName("여러 itemId로부터 성공적으로 Item 리스트를 반환한다")
    void should_return_item_list_when_fetch_successful() throws Exception {
        // given
        Set<Long> itemIds = Set.of(1L, 2L);

        ItemFetchResult itemResult1 = mock(ItemFetchResult.class);
        ItemFetchResult itemResult2 = mock(ItemFetchResult.class);
        MediaFetchResult mediaResult1 = mock(MediaFetchResult.class);
        MediaFetchResult mediaResult2 = mock(MediaFetchResult.class);
        Item item1 = mock(Item.class);
        Item item2 = mock(Item.class);

        // 각 아이템 fetchAsync 반환값 설정
        given(itemFetcher.fetchItemAsync(1L)).willReturn(CompletableFuture.completedFuture(itemResult1));
        given(itemFetcher.fetchItemAsync(2L)).willReturn(CompletableFuture.completedFuture(itemResult2));
        given(mediaFetcher.fetchMediaAsync(1L)).willReturn(CompletableFuture.completedFuture(mediaResult1));
        given(mediaFetcher.fetchMediaAsync(2L)).willReturn(CompletableFuture.completedFuture(mediaResult2));

        // ItemFactory.create 호출 시 mock Item 반환
        given(itemFactory.create(itemResult1, mediaResult1)).willReturn(item1);
        given(itemFactory.create(itemResult2, mediaResult2)).willReturn(item2);

        // when
        List<Item> items = fetchItemService.fetchItemAsync(itemIds);

        // then
        assertThat(items).containsExactlyInAnyOrder(item1, item2);
    }

    @Test
    @DisplayName("fetch 중 ItemFetchException 예외가 발생하면 해당 아이템은 null 처리되어 리스트에서 제외된다")
    void should_exclude_null_items_when_exception_occurs() throws Exception {
        // given
        Set<Long> itemIds = Set.of(1L, 2L);

        ItemFetchResult itemResult1 = mock(ItemFetchResult.class);
        MediaFetchResult mediaResult1 = mock(MediaFetchResult.class);
        Item item1 = mock(Item.class);

        given(itemFetcher.fetchItemAsync(1L)).willReturn(CompletableFuture.completedFuture(itemResult1));
        given(mediaFetcher.fetchMediaAsync(1L)).willReturn(CompletableFuture.completedFuture(mediaResult1));
        given(itemFactory.create(itemResult1, mediaResult1)).willReturn(item1);

        // 2번 아이템은 예외 발생 (fetchItemAsync 내부에서 exceptionally로 null 반환)
        given(itemFetcher.fetchItemAsync(2L)).willReturn(CompletableFuture.failedFuture(
                        new ItemFetchException("Fetch failed", new RuntimeException(), 500, 100L)
                )
        );
        given(mediaFetcher.fetchMediaAsync(2L)).willReturn(CompletableFuture.completedFuture(mock(MediaFetchResult.class)));

        // when
        List<Item> items = fetchItemService.fetchItemAsync(itemIds);

        // then
        assertThat(items).containsExactly(item1);
    }

    @Test
    @DisplayName("fetch 중 예상하지 못한 예외가 발생하면")
    void should_return_exception_when_unexpected_exception_occurs() throws Exception {
        // given
        Set<Long> itemIds = Set.of(1L, 2L);

        ItemFetchResult itemResult1 = mock(ItemFetchResult.class);
        MediaFetchResult mediaResult1 = mock(MediaFetchResult.class);
        Item item1 = mock(Item.class);

        given(itemFetcher.fetchItemAsync(1L)).willReturn(CompletableFuture.completedFuture(itemResult1));
        given(mediaFetcher.fetchMediaAsync(1L)).willReturn(CompletableFuture.completedFuture(mediaResult1));
        given(itemFactory.create(itemResult1, mediaResult1)).willReturn(item1);

        // 2번 아이템은 예외 발생 (fetchItemAsync 내부에서 exceptionally로 null 반환)
        given(itemFetcher.fetchItemAsync(2L)).willReturn(CompletableFuture.failedFuture(new RuntimeException("Fetch failed")));
        given(mediaFetcher.fetchMediaAsync(2L)).willReturn(CompletableFuture.completedFuture(mock(MediaFetchResult.class)));

        // when then
        assertThatThrownBy(() -> fetchItemService.fetchItemAsync(itemIds))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("InterruptedException 발생 시 인터럽트 상태를 복원하고 예외를 던진다")
    void should_propagate_interrupted_exception_and_restore_interrupt_status() {
        // given
        Set<Long> itemIds = Set.of(1L);

        ItemFetchResult itemResult1 = mock(ItemFetchResult.class);
        MediaFetchResult mediaResult1 = mock(MediaFetchResult.class);
        given(itemFetcher.fetchItemAsync(1L)).willReturn(CompletableFuture.completedFuture(itemResult1));
        given(mediaFetcher.fetchMediaAsync(1L)).willReturn(CompletableFuture.completedFuture(mediaResult1));

        // when
        Thread testThread = Thread.currentThread();

        CompletableFuture.runAsync(() -> {
            try {
                fetchItemService.fetchItemAsync(itemIds);
                fail("Expected InterruptedException not thrown");
            } catch (InterruptedException e) {
                // then
                assertThat(Thread.currentThread().isInterrupted()).isTrue();
            }
        });

        // 인위적으로 인터럽트 발생
        testThread.interrupt();
    }
}
