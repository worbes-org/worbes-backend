package com.worbes.adapter.blizzard.data.item;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.application.item.port.out.ItemFetchResult;
import com.worbes.application.item.port.out.ItemFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemFetcherImpl implements ItemFetcher {

    private final BlizzardApiClient apiClient;
    private final ItemResponseMapper responseMapper;
    private final BlizzardApiUriFactory uriFactory;
    private final ItemFetchExceptionHandler exceptionHandler;

    @Override
    public CompletableFuture<ItemFetchResult> fetchItemAsync(Long id) {
        URI uri = uriFactory.itemUri(id);

        return apiClient.fetchAsync(uri, ItemResponse.class)
                .thenApply(responseMapper::toDto)
                .exceptionally(exceptionHandler.handle("Item", id));
    }
}
