package com.worbes.application.core.item.port;

import com.worbes.application.core.item.dto.ItemClassDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ItemFetcher {
    CompletableFuture<List<ItemClassDto>> fetchItemClassesIndex();
}
