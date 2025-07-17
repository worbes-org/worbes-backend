package com.worbes.application.item.port.out;

import java.util.concurrent.CompletableFuture;

public interface FetchExtraItemInfoPort {
    CompletableFuture<FetchExtraItemInfoResult> fetchAsync(Long id);
}
