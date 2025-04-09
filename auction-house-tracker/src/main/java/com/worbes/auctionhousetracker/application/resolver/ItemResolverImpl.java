package com.worbes.auctionhousetracker.application.resolver;

import com.worbes.auctionhousetracker.application.fetcher.ItemFetcher;
import com.worbes.auctionhousetracker.dto.mapper.ItemSaveCommandMapper;
import com.worbes.auctionhousetracker.dto.response.ItemMediaResponse;
import com.worbes.auctionhousetracker.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
@Slf4j
public class ItemResolverImpl implements ItemResolver {

    private final ItemService itemService;
    private final ItemFetcher itemFetcher;
    private final ItemSaveCommandMapper mapper;

    @Override
    public void resolveItems(List<Long> itemIds) {
        Set<Long> missingItemIds = itemService.findMissingItemIds(itemIds);
        List<CompletableFuture<ItemMediaResponse>> futures = itemFetcher.fetchItemsAsync(new HashSet<>(missingItemIds));
        List<ItemMediaResponse> responses = collectValidResponses(futures);
        itemService.saveAll(responses.stream().map(mapper::toCommand).toList());
    }

    private List<ItemMediaResponse> collectValidResponses(List<CompletableFuture<ItemMediaResponse>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .toList()
                )
                .join();
    }
}
