package com.worbes.application.item.service;

import com.worbes.application.item.exception.ItemFetchException;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.out.ItemFetchResult;
import com.worbes.application.item.port.out.ItemFetcher;
import com.worbes.application.item.port.out.MediaFetchResult;
import com.worbes.application.item.port.out.MediaFetcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ItemFetchServiceTest {

    @Mock
    private ItemFetcher itemFetcher;

    @Mock
    private MediaFetcher mediaFetcher;

    @Mock
    private ItemFactory itemFactory;

    @InjectMocks
    private ItemFetchService itemFetchService;

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("여러 itemId로부터 성공적으로 Item 리스트를 반환한다")
        void should_return_item_list_when_fetch_successful() throws Exception {
            Set<Long> itemIds = Set.of(1L, 2L);
            ItemFetchResult itemResult1 = mock(ItemFetchResult.class);
            ItemFetchResult itemResult2 = mock(ItemFetchResult.class);
            MediaFetchResult mediaResult1 = mock(MediaFetchResult.class);
            MediaFetchResult mediaResult2 = mock(MediaFetchResult.class);

            Item item1 = mock(Item.class);
            Item item2 = mock(Item.class);

            given(itemFetcher.fetchItemAsync(1L)).willReturn(CompletableFuture.completedFuture(itemResult1));
            given(itemFetcher.fetchItemAsync(2L)).willReturn(CompletableFuture.completedFuture(itemResult2));
            given(mediaFetcher.fetchMediaAsync(1L)).willReturn(CompletableFuture.completedFuture(mediaResult1));
            given(mediaFetcher.fetchMediaAsync(2L)).willReturn(CompletableFuture.completedFuture(mediaResult2));
            given(itemFactory.create(itemResult1, mediaResult1)).willReturn(item1);
            given(itemFactory.create(itemResult2, mediaResult2)).willReturn(item2);

            List<Item> items = itemFetchService.fetchItemAsync(itemIds);

            assertThat(items).containsExactlyInAnyOrder(item1, item2);
        }

        @Test
        @DisplayName("빈 Set 입력 시 빈 리스트 반환")
        void should_return_empty_list_when_input_is_empty() throws Exception {
            Set<Long> itemIds = Set.of();
            List<Item> items = itemFetchService.fetchItemAsync(itemIds);
            assertThat(items).isEmpty();
        }
    }

    @Nested
    @DisplayName("경계 케이스")
    class EdgeCases {
        @Test
        @DisplayName("fetch 중 ItemFetchException 예외가 발생하면 해당 아이템은 null 처리되어 리스트에서 제외된다")
        void should_exclude_null_items_when_exception_occurs() throws Exception {
            Set<Long> itemIds = Set.of(1L, 2L);
            ItemFetchResult itemResult1 = mock(ItemFetchResult.class);
            MediaFetchResult mediaResult1 = mock(MediaFetchResult.class);

            Item item1 = mock(Item.class);
            given(itemFetcher.fetchItemAsync(1L)).willReturn(CompletableFuture.completedFuture(itemResult1));
            given(mediaFetcher.fetchMediaAsync(1L)).willReturn(CompletableFuture.completedFuture(mediaResult1));
            given(itemFactory.create(itemResult1, mediaResult1)).willReturn(item1);
            given(itemFetcher.fetchItemAsync(2L)).willReturn(CompletableFuture.failedFuture(
                    new ItemFetchException("Fetch failed", new RuntimeException(), 500, 100L)));
            given(mediaFetcher.fetchMediaAsync(2L)).willReturn(CompletableFuture.completedFuture(mock(MediaFetchResult.class)));

            List<Item> items = itemFetchService.fetchItemAsync(itemIds);

            assertThat(items).containsExactly(item1);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Test
        @DisplayName("fetch 중 예상하지 못한 예외가 발생하면 ExecutionException throw")
        void should_return_exception_when_unexpected_exception_occurs() throws Exception {
            Set<Long> itemIds = Set.of(1L, 2L);
            ItemFetchResult itemResult1 = mock(ItemFetchResult.class);
            MediaFetchResult mediaResult1 = mock(MediaFetchResult.class);
            Item item1 = mock(Item.class);
            given(itemFetcher.fetchItemAsync(1L)).willReturn(CompletableFuture.completedFuture(itemResult1));
            given(mediaFetcher.fetchMediaAsync(1L)).willReturn(CompletableFuture.completedFuture(mediaResult1));
            given(itemFactory.create(itemResult1, mediaResult1)).willReturn(item1);
            given(itemFetcher.fetchItemAsync(2L)).willReturn(CompletableFuture.failedFuture(new RuntimeException("Fetch failed")));
            given(mediaFetcher.fetchMediaAsync(2L)).willReturn(CompletableFuture.completedFuture(mock(MediaFetchResult.class)));

            assertThatThrownBy(() -> itemFetchService.fetchItemAsync(itemIds))
                    .isInstanceOf(ExecutionException.class);
        }

        @Test
        @DisplayName("InterruptedException 발생 시 인터럽트 상태를 복원하고 예외를 던진다")
        void should_propagate_interrupted_exception_and_restore_interrupt_status() {
            Set<Long> itemIds = Set.of(1L);
            Thread testThread = Thread.currentThread();
            CompletableFuture.runAsync(() -> {
                try {
                    itemFetchService.fetchItemAsync(itemIds);
                } catch (InterruptedException e) {
                    assertThat(Thread.currentThread().isInterrupted()).isTrue();
                } catch (Exception e) {
                    // ignore
                }
            });
            testThread.interrupt();
        }

        @Test
        @DisplayName("TimeoutException 발생 시 예외 전파")
        void should_throw_timeout_exception() throws Exception {
            Set<Long> itemIds = Set.of(1L);
            given(itemFetcher.fetchItemAsync(1L)).willReturn(CompletableFuture.failedFuture(new TimeoutException()));
            given(mediaFetcher.fetchMediaAsync(1L)).willReturn(CompletableFuture.completedFuture(mock(MediaFetchResult.class)));
            assertThatThrownBy(() -> itemFetchService.fetchItemAsync(itemIds))
                    .isInstanceOf(ExecutionException.class);
        }
    }
}
