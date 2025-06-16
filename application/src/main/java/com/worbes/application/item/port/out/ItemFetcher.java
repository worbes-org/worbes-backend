package com.worbes.application.item.port.out;

import java.util.concurrent.CompletableFuture;

public interface ItemFetcher {
    CompletableFuture<ItemFetchResult> fetchItemAsync(Long id);
}

