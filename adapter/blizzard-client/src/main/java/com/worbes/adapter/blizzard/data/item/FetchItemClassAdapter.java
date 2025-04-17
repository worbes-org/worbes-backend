package com.worbes.adapter.blizzard.data.item;

import com.worbes.adapter.blizzard.client.BlizzardApiRestClient;
import com.worbes.adapter.blizzard.data.shared.QueryParamsBuilder;
import com.worbes.application.initializer.FetchItemClassPort;
import com.worbes.application.initializer.ItemClassDto;
import com.worbes.application.initializer.ItemClassIndexDto;
import com.worbes.domain.shared.RegionType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class FetchItemClassAdapter implements FetchItemClassPort {

    private final BlizzardApiRestClient apiClient;

    public List<ItemClassIndexDto> fetchItemClassesIndex(RegionType region) {
        String url = ItemApiUrlFactory.itemClassesIndexUrl(region);
        Map<String, String> params = QueryParamsBuilder.builder(region).staticNamespace().build();
        ItemClassesIndexResponse response = apiClient.fetch(url, params, ItemClassesIndexResponse.class);

        return ItemClassIndexDtoMapper.INSTANCE.toDtoList(response.getItemClasses());
    }

    public CompletableFuture<ItemClassDto> fetchItemClass(RegionType region, Long itemClassId) {
        String url = ItemApiUrlFactory.itemClassUrl(region, itemClassId);
        Map<String, String> params = QueryParamsBuilder.builder(region).staticNamespace().build();

        return CompletableFuture.supplyAsync(() -> apiClient.fetch(url, params, ItemClassResponse.class))
                .thenApply(ItemClassDtoMapper.INSTANCE::toDto);
    }
}
