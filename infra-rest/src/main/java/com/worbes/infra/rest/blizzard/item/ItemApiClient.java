package com.worbes.infra.rest.blizzard.item;

import com.worbes.domain.shared.RegionType;
import com.worbes.infra.rest.blizzard.BlizzardApiClient;
import com.worbes.infra.rest.blizzard.support.QueryParamsBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class ItemApiClient {

    private final BlizzardApiClient apiClient;

    public CompletableFuture<ItemClassesIndexResponse> fetchItemClassesIndex(RegionType region) {
        String url = ItemApiUrlFactory.itemClassesIndexUrl(region);
        Map<String, String> params = QueryParamsBuilder.builder(region).staticNamespace().build();
        return CompletableFuture.supplyAsync(() -> apiClient.fetch(url, params, ItemClassesIndexResponse.class));
    }

    public CompletableFuture<ItemClassResponse> fetchItemClass(RegionType region, Long itemClassId) {
        String url = ItemApiUrlFactory.itemClassUrl(region, itemClassId);
        Map<String, String> params = QueryParamsBuilder.builder(region).staticNamespace().build();
        return CompletableFuture.supplyAsync(() -> apiClient.fetch(url, params, ItemClassResponse.class));
    }
}
