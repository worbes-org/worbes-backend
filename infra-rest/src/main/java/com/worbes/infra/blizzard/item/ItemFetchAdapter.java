package com.worbes.infra.blizzard.item;

import com.worbes.application.core.item.dto.ItemClassDto;
import com.worbes.application.core.item.port.ItemFetcher;
import com.worbes.domain.shared.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Component
@RequiredArgsConstructor
@Slf4j
public class ItemFetchAdapter implements ItemFetcher {

    private final ItemApiClient itemClient;
    private final ItemClassDtoMapper mapper;

    public CompletableFuture<List<ItemClassDto>> fetchItemClassesIndex() {
        return itemClient.fetchItemClassesIndex(RegionType.KR)
                .thenApply(ItemClassesIndexResponse::getItemClasses)
                .thenApply(mapper::toDtoList);
    }
}
