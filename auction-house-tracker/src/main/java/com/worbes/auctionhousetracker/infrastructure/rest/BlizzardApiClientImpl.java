package com.worbes.auctionhousetracker.infrastructure.rest;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.*;
import com.worbes.auctionhousetracker.entity.enums.Region;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.DYNAMIC;
import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.STATIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlizzardApiClientImpl implements BlizzardApiClient {

    private final RestApiClient restApiClient;
    private final ThreadPoolTaskExecutor asyncExecutor;

    @Override
    public AuctionResponse fetchCommodities(Region region) {
        return restApiClient.get(
                BlizzardApiUrlBuilder.builder(region).commodities().build(),
                BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build(),
                AuctionResponse.class
        );
    }

    @Override
    public AuctionResponse fetchAuctions(Region region, Long realmId) {
        return restApiClient.get(
                BlizzardApiUrlBuilder.builder(region).auctions(realmId).build(),
                BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build(),
                AuctionResponse.class);
    }

    @Override
    public ItemClassesIndexResponse fetchItemClassesIndex() {
        Region region = Region.US;
        String path = BlizzardApiUrlBuilder.builder(region).itemClassIndex().build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        return restApiClient.get(path, params, ItemClassesIndexResponse.class);
    }

    @Override
    public ItemClassResponse fetchItemClass(Long itemClassId) {
        Region region = Region.US;
        String url = BlizzardApiUrlBuilder.builder(region).itemClass(itemClassId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        return restApiClient.get(url, params, ItemClassResponse.class);
    }

    @Override
    public ItemSubclassResponse fetchItemSubclass(Long itemClassId, Long subclassId) {
        Region region = Region.US;
        String url = BlizzardApiUrlBuilder.builder(region).itemSubclass(itemClassId, subclassId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        return restApiClient.get(url, params, ItemSubclassResponse.class);
    }

    @Override
    public ItemResponse fetchItem(Long itemId) {
        Region region = Region.US;
        String path = BlizzardApiUrlBuilder.builder(region).item(itemId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        return restApiClient.get(path, params, ItemResponse.class);
    }

    public CompletableFuture<ItemResponse> fetchItemAsync(Long itemId) {
        return CompletableFuture.supplyAsync(() -> fetchItem(itemId), asyncExecutor);
    }

    public CompletableFuture<MediaResponse> fetchItemMediaAsync(Long itemId) {
        return CompletableFuture.supplyAsync(() -> fetchItemMedia(itemId), asyncExecutor);
    }

    @Override
    public CompletableFuture<ItemMediaResponse> fetchItemWithMediaAsync(Long itemId) {
        return fetchItemAsync(itemId)
                .exceptionally(throwable -> {
                    log.warn("❌ fetchItemAsync error itemId={} | 원인={}", itemId, throwable.getMessage());
                    return null;
                })
                .thenCombine(fetchItemMediaAsync(itemId), ItemMediaResponse::new)
                .exceptionally(throwable -> {
                    log.warn("❌ fetchItemMediaAsync error itemId={} | 원인={}", itemId, throwable.getMessage());
                    return null;
                });

    }

    @Override
    public MediaResponse fetchItemMedia(Long itemId) {
        Region region = Region.US;
        String path = BlizzardApiUrlBuilder.builder(region).media(itemId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        return restApiClient.get(path, params, MediaResponse.class);
    }

    @Override
    public RealmIndexResponse fetchRealmIndex(Region region) {
        return restApiClient.get(
                BlizzardApiUrlBuilder.builder(region).realmIndex().build(),
                BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build(),
                RealmIndexResponse.class
        );
    }

    @Override
    public RealmResponse fetchRealm(Region region, String slug) {
        return restApiClient.get(
                BlizzardApiUrlBuilder.builder(region).realm(slug).build(),
                BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build(),
                RealmResponse.class
        );
    }
}
