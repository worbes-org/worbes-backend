package com.worbes.application.item.port.out;

import java.util.concurrent.CompletableFuture;

public interface MediaFetcher {
    CompletableFuture<MediaFetchResult> fetchMediaAsync(Long itemId);
}
