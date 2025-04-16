package com.worbes.application.initializer;

import com.worbes.domain.shared.RegionType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FetchItemClassPort {
    List<ItemClassIndexDto> fetchItemClassesIndex(RegionType region);

    CompletableFuture<ItemClassDto> fetchItemClass(RegionType region, Long itemClassId);
}
