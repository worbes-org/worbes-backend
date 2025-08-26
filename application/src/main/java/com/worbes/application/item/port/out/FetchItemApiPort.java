package com.worbes.application.item.port.out;

import com.worbes.application.item.model.Item;

import java.util.concurrent.CompletableFuture;

public interface FetchItemApiPort {
    CompletableFuture<Item> fetchAsync(Long id);
}

