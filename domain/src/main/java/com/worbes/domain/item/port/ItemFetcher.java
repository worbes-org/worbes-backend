package com.worbes.domain.item.port;

import com.worbes.domain.item.dto.ItemClassDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ItemFetcher {
    CompletableFuture<List<ItemClassDto>> fetchItemClassesIndex();
}
