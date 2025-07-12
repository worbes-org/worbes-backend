package com.worbes.adapter.blizzard.data.item;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.out.ItemApiFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemApiFetcherImpl implements ItemApiFetcher {

    private final BlizzardApiClient apiClient;
    private final ItemResponseMapper responseMapper;
    private final BlizzardApiUriFactory uriFactory;
    private final ItemFetchExceptionHandler exceptionHandler;

    @Override
    public CompletableFuture<Item> fetchItemAsync(Long id) {
        URI uri = uriFactory.itemUri(id);

        return apiClient.fetchAsync(uri, ItemResponse.class)
                .thenApply(responseMapper::toDto)
                .exceptionally(exceptionHandler.handle("Item", id));
    }

    @Override
    public CompletableFuture<String> fetchMediaAsync(Long itemId) {
        URI uri = uriFactory.mediaUri(itemId);

        return apiClient.fetchAsync(uri, MediaResponse.class)
                .thenApply(MediaResponse::getIconUrl)
                .thenApply(this::extractIconName)
                .exceptionally(exceptionHandler.handle("Media", itemId));
    }

    private String extractIconName(String url) {
        String fileName = url.substring(url.lastIndexOf('/') + 1);

        return fileName.contains(".")
                ? fileName.substring(0, fileName.lastIndexOf('.'))
                : fileName;
    }
}
