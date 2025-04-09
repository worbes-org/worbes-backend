package com.worbes.auctionhousetracker.application.fetcher;

import com.worbes.auctionhousetracker.dto.response.ItemClassResponse;
import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.dto.response.ItemMediaResponse;
import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ItemFetcher {
    List<CompletableFuture<ItemMediaResponse>> fetchItemsAsync(Set<Long> itemIds);

    ItemClassesIndexResponse fetchItemClassesIndex();

    ItemClassResponse fetchItemClass(Long itemClassId);

    CompletableFuture<ItemSubclassResponse> fetchItemSubclassAsync(Long itemClassId, Long subclassId);
}
