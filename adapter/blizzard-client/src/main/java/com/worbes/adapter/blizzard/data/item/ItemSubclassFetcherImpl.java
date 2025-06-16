package com.worbes.adapter.blizzard.data.item;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.application.item.port.out.ItemSubclassFetchResult;
import com.worbes.application.item.port.out.ItemSubclassFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class ItemSubclassFetcherImpl implements ItemSubclassFetcher {

    private final BlizzardApiClient apiClient;
    private final ItemSubclassResponseMapper responseMapper;
    private final BlizzardApiUriFactory uriFactory;

    @Override
    public CompletableFuture<ItemSubclassFetchResult> fetchItemSubclassAsync(Long itemClassId, Long subclassId) {
        URI uri = uriFactory.itemSubclassUri(itemClassId, subclassId);

        return apiClient.fetchAsync(uri, ItemSubclassResponse.class)
                .thenApply(responseMapper::toDto);
    }

    @Override
    public List<CompletableFuture<ItemSubclassFetchResult>> fetchItemSubclassAsync(Long itemClassId, Collection<Long> subclassIds) {
        return subclassIds.stream()
                .map(subclassId -> fetchItemSubclassAsync(itemClassId, subclassId))
                .toList();
    }
}
