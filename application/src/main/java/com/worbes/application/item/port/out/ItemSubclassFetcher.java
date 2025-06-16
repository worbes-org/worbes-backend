package com.worbes.application.item.port.out;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ItemSubclassFetcher {
    CompletableFuture<ItemSubclassFetchResult> fetchItemSubclassAsync(Long itemClassId, Long subclassId);

    List<CompletableFuture<ItemSubclassFetchResult>> fetchItemSubclassAsync(Long itemClassId, Collection<Long> subclassIds);
}
