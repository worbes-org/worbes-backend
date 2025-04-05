package com.worbes.auctionhousetracker.application.fetcher;

import com.worbes.auctionhousetracker.dto.response.ItemMediaResponse;

import java.util.concurrent.CompletableFuture;

public interface ItemFetcher {
    CompletableFuture<ItemMediaResponse> fetchItemWithMediaAsync(Long itemId);
}
