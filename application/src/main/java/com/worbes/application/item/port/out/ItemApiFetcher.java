package com.worbes.application.item.port.out;

import com.worbes.application.item.model.Item;

import java.util.concurrent.CompletableFuture;

public interface ItemApiFetcher {
    CompletableFuture<Item> fetchItemAsync(Long id);

    CompletableFuture<String> fetchMediaAsync(Long itemId);
}

