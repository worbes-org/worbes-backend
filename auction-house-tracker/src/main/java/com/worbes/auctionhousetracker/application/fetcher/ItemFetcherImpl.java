package com.worbes.auctionhousetracker.application.fetcher;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.*;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.STATIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemFetcherImpl implements ItemFetcher {

    private static final RegionType KR = RegionType.KR;
    private final RestApiClient restApiClient;
    private final ThreadPoolTaskExecutor asyncExecutor;

    @Override
    public ItemClassesIndexResponse fetchItemClassesIndex() {
        String path = BlizzardApiUrlBuilder.builder(KR).itemClassIndex().build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(KR).namespace(STATIC).build();
        return restApiClient.get(path, params, ItemClassesIndexResponse.class);
    }

    @Override
    public ItemClassResponse fetchItemClass(Long itemClassId) {
        String url = BlizzardApiUrlBuilder.builder(KR).itemClass(itemClassId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(KR).namespace(STATIC).build();
        return restApiClient.get(url, params, ItemClassResponse.class);
    }

    @Override
    public CompletableFuture<ItemSubclassResponse> fetchItemSubclassAsync(Long itemClassId, Long subclassId) {
        return CompletableFuture.supplyAsync(() -> fetchItemSubclass(itemClassId, subclassId), asyncExecutor);
    }

    private ItemSubclassResponse fetchItemSubclass(Long itemClassId, Long subclassId) {
        String url = BlizzardApiUrlBuilder.builder(KR).itemSubclass(itemClassId, subclassId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(KR).namespace(STATIC).build();
        return restApiClient.get(url, params, ItemSubclassResponse.class);
    }

    @Override
    public List<CompletableFuture<ItemMediaResponse>> fetchItemsAsync(Set<Long> itemIds) {
        return itemIds.stream()
                .map(this::fetchItemWithMediaAsync)
                .toList();
    }

    private CompletableFuture<ItemMediaResponse> fetchItemWithMediaAsync(Long itemId) {
        return fetchItemAsync(itemId)
                .thenCompose(itemResponse ->
                        fetchItemMediaAsync(itemId)
                                .thenApply(mediaResponse -> new ItemMediaResponse(itemResponse, mediaResponse))
                )
                .exceptionally(throwable -> {
                    log.warn("❌ fetchItemWithMediaAsync error itemId={} | 원인={}", itemId, throwable.getMessage());
                    return null;
                });
    }

    private MediaResponse fetchItemMedia(Long itemId) {
        String path = BlizzardApiUrlBuilder.builder(KR).media(itemId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(KR).namespace(STATIC).build();
        return restApiClient.get(path, params, MediaResponse.class);
    }

    private CompletableFuture<MediaResponse> fetchItemMediaAsync(Long itemId) {
        return CompletableFuture.supplyAsync(() -> fetchItemMedia(itemId), asyncExecutor);

    }

    private ItemResponse fetchItem(Long itemId) {
        String path = BlizzardApiUrlBuilder.builder(KR).item(itemId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(KR).namespace(STATIC).build();
        return restApiClient.get(path, params, ItemResponse.class);
    }

    private CompletableFuture<ItemResponse> fetchItemAsync(Long itemId) {
        return CompletableFuture.supplyAsync(() -> fetchItem(itemId), asyncExecutor);
    }
}
