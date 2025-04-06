package com.worbes.auctionhousetracker.application.fetcher;

import com.worbes.auctionhousetracker.dto.response.ItemMediaResponse;

import java.util.concurrent.CompletableFuture;

public class ItemFetcherImpl implements ItemFetcher {
    @Override
    public CompletableFuture<ItemMediaResponse> fetchItemWithMediaAsync(Long itemId) {
        return null;
    }
}
