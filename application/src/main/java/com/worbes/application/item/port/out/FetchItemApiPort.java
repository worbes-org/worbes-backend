package com.worbes.application.item.port.out;

import java.util.concurrent.CompletableFuture;

public interface FetchItemApiPort {
    CompletableFuture<FetchItemApiResult> fetchAsync(Long id);
}

