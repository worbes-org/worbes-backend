package com.worbes.adapter.blizzard.data.item;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.client.BlizzardApiException;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.adapter.blizzard.data.shared.BlizzardResponseValidator;
import com.worbes.application.item.exception.ItemApiFetchException;
import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.model.QualityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class FetchItemApiPortAdapterTest {

    @Mock
    private BlizzardApiClient apiClient;

    @Mock
    private BlizzardApiUriFactory uriFactory;

    @Mock
    private ItemJsonLoader itemJsonLoader;

    @Mock
    private BlizzardResponseValidator validator;

    @InjectMocks
    private FetchItemApiAdapter adapter;

    @Test
    @DisplayName("정상 케이스 - convertToDomain으로 올바른 Item 반환")
    void givenValidResponses_whenFetchAsync_thenReturnItem() throws Exception {
        // given
        Long itemId = 100L;
        URI uri = URI.create("http://example.com/item/" + itemId);

        ItemBlizzardApiResponse blizzardResponse = new ItemBlizzardApiResponse(
                itemId,
                Map.of("en_US", "Sword of Testing"),
                new ItemBlizzardApiResponse.Quality("RARE"),
                new ItemBlizzardApiResponse.InventoryType("WEAPON"),
                new ItemBlizzardApiResponse.ItemClass(2L),
                new ItemBlizzardApiResponse.ItemSubclass(7L),
                50,
                true
        );

        ItemWowHeadApiResponse wowheadResponse = new ItemWowHeadApiResponse("icon.png", 1, 2, 3L);

        given(uriFactory.itemUri(itemId)).willReturn(uri);
        given(apiClient.fetchAsync(uri, ItemBlizzardApiResponse.class))
                .willReturn(CompletableFuture.completedFuture(blizzardResponse));
        given(validator.validate(blizzardResponse)).willReturn(blizzardResponse);
        given(itemJsonLoader.fetchAsync(itemId))
                .willReturn(CompletableFuture.completedFuture(wowheadResponse));

        // when
        Item actualItem = adapter.fetchAsync(itemId).get();

        // then
        assertThat(actualItem.getId()).isEqualTo(itemId);
        assertThat(actualItem.getName()).isEqualTo(Map.of("en_US", "Sword of Testing"));
        assertThat(actualItem.getLevel()).isEqualTo(50);
        assertThat(actualItem.getClassId()).isEqualTo(2L);
        assertThat(actualItem.getSubclassId()).isEqualTo(7L);
        assertThat(actualItem.getQuality()).isEqualTo(QualityType.RARE);
        assertThat(actualItem.getIsStackable()).isTrue();
        assertThat(actualItem.getInventoryType()).isEqualTo(InventoryType.WEAPON);
        assertThat(actualItem.getIcon()).isEqualTo("icon.png");
        assertThat(actualItem.getExpansionId()).isEqualTo(1);
        assertThat(actualItem.getCraftingTier()).isEqualTo(CraftingTierType.SECOND);
        assertThat(actualItem.getDisplayId()).isEqualTo(3L);

        then(uriFactory).should(times(1)).itemUri(itemId);
        then(apiClient).should(times(1)).fetchAsync(uri, ItemBlizzardApiResponse.class);
        then(validator).should(times(1)).validate(blizzardResponse);
        then(itemJsonLoader).should(times(1)).fetchAsync(itemId);
    }

    @Test
    @DisplayName("BlizzardApiException 발생 시 ItemApiFetchException으로 변환")
    void givenBlizzardApiException_whenFetchAsync_thenThrowItemApiFetchException() {
        // given
        Long itemId = 101L;
        URI uri = URI.create("http://example.com/item/" + itemId);
        BlizzardApiException exception = new BlizzardApiException("fail", 500);

        given(uriFactory.itemUri(itemId)).willReturn(uri);
        given(apiClient.fetchAsync(uri, ItemBlizzardApiResponse.class))
                .willReturn(CompletableFuture.failedFuture(exception));
        given(itemJsonLoader.fetchAsync(itemId))
                .willReturn(CompletableFuture.completedFuture(new ItemWowHeadApiResponse("icon.png", 1, 2, 3L)));

        // when & then
        assertThatThrownBy(() -> adapter.fetchAsync(itemId).join())
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(ItemApiFetchException.class);

        then(uriFactory).should(times(1)).itemUri(itemId);
        then(apiClient).should(times(1)).fetchAsync(uri, ItemBlizzardApiResponse.class);
    }

    @Test
    @DisplayName("알 수 없는 예외 발생 시 ItemApiFetchException으로 변환")
    void givenUnknownException_whenFetchAsync_thenThrowItemApiFetchException() {
        // given
        Long itemId = 102L;
        URI uri = URI.create("http://example.com/item/" + itemId);
        RuntimeException exception = new RuntimeException("unknown");

        given(uriFactory.itemUri(itemId)).willReturn(uri);
        given(apiClient.fetchAsync(uri, ItemBlizzardApiResponse.class))
                .willReturn(CompletableFuture.failedFuture(exception));
        given(itemJsonLoader.fetchAsync(itemId))
                .willReturn(CompletableFuture.completedFuture(new ItemWowHeadApiResponse("icon.png", 1, 2, 3L)));

        // when & then
        assertThatThrownBy(() -> adapter.fetchAsync(itemId).join())
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(ItemApiFetchException.class)
                .hasMessageContaining("unknown");
    }
}

