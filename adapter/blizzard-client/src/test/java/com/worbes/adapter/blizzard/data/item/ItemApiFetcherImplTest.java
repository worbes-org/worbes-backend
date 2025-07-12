package com.worbes.adapter.blizzard.data.item;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.application.item.exception.ItemApiFetchException;
import com.worbes.application.item.model.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ItemApiFetcherImplTest {

    @Mock
    private BlizzardApiClient apiClient;

    @Mock
    private ItemResponseMapper responseMapper;

    @Mock
    private BlizzardApiUriFactory uriFactory;

    @Mock
    private ItemFetchExceptionHandler exceptionHandler;

    @InjectMocks
    private ItemApiFetcherImpl itemFetcher;

    @Test
    @DisplayName("성공적으로 ItemFetchResult를 반환한다")
    void should_return_ItemFetchResult_when_fetch_successful() {
        // given
        Long itemId = 123L;
        URI uri = URI.create("https://example.com/item/123");

        ItemResponse itemResponse = mock(ItemResponse.class);
        Item fetchResult = mock(Item.class);

        given(uriFactory.itemUri(itemId)).willReturn(uri);
        given(apiClient.fetchAsync(uri, ItemResponse.class))
                .willReturn(CompletableFuture.completedFuture(itemResponse));
        given(responseMapper.toDto(itemResponse)).willReturn(fetchResult);
        given(exceptionHandler.handle("Item", itemId)).willReturn(throwable -> {
            throw new CompletionException(new ItemApiFetchException("fail", throwable, 500, itemId));
        });


        // when
        CompletableFuture<Item> future = itemFetcher.fetchItemAsync(itemId);

        // then
        assertThat(future).succeedsWithin(Duration.ofSeconds(1))
                .isEqualTo(fetchResult);
    }

    @Test
    @DisplayName("예외 발생 시 예외 처리 핸들러가 호출된다")
    void should_handle_exception_when_fetch_fails() {
        // given
        Long itemId = 456L;
        URI uri = URI.create("https://example.com/item/456");

        RuntimeException cause = new RuntimeException("API failure");

        given(uriFactory.itemUri(itemId)).willReturn(uri);
        given(apiClient.fetchAsync(uri, ItemResponse.class))
                .willReturn(CompletableFuture.failedFuture(cause));
        given(exceptionHandler.handle("Item", itemId)).willReturn(throwable -> {
            throw new CompletionException(new ItemApiFetchException("fail", throwable, 500, itemId));
        });

        // when
        CompletableFuture<Item> future = itemFetcher.fetchItemAsync(itemId);

        // then
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(ItemApiFetchException.class)
                .hasMessageContaining("fail");
    }
}
