package com.worbes.infra.rest.blizzard.item;

import com.worbes.application.initializer.FetchItemClassPort;
import com.worbes.application.initializer.ItemClassDto;
import com.worbes.application.initializer.ItemClassIndexDto;
import com.worbes.domain.shared.RegionType;
import com.worbes.infra.rest.blizzard.BlizzardApiClient;
import com.worbes.infra.rest.blizzard.support.QueryParamsBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class FetchItemClassAdapter implements FetchItemClassPort {

    private final BlizzardApiClient apiClient;

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
