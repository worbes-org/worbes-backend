package com.worbes.adapter.blizzard.data.item;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.application.item.port.out.MediaFetchResult;
import com.worbes.application.item.port.out.MediaFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class MediaFetcherImpl implements MediaFetcher {

    private final BlizzardApiClient apiClient;
    private final BlizzardApiUriFactory uriFactory;
    private final ItemFetchExceptionHandler exceptionHandler;

    @Override
    public CompletableFuture<MediaFetchResult> fetchMediaAsync(Long itemId) {
        URI uri = uriFactory.mediaUri(itemId);

        return apiClient.fetchAsync(uri, MediaResponse.class)
                .thenApply(response -> new MediaFetchResult(response.getIconUrl()))
                .exceptionally(exceptionHandler.handle("Media", itemId));
    }
}
