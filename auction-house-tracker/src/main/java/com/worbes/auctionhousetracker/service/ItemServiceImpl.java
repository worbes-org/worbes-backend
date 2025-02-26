package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.ItemResponse;
import com.worbes.auctionhousetracker.dto.response.MediaResponse;
import com.worbes.auctionhousetracker.entity.Item;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.STATIC;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final RestApiClient restApiClient;

    @Override
    public Item collectItemWithMedia(Long itemId) {
        CompletableFuture<ItemResponse> itemFuture = CompletableFuture
                .supplyAsync(() -> fetchItemData(itemId));

        CompletableFuture<String> mediaFuture = CompletableFuture
                .supplyAsync(() -> fetchItemIconUrl(itemId));

        try {
            ItemResponse itemResponse = itemFuture.get();
            String iconUrl = mediaFuture.get();
            return Item.from(itemResponse, iconUrl);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while fetching item data", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to fetch item data: " + e.getCause().getMessage(), e.getCause());
        }
    }

    public ItemResponse fetchItemData(Long itemId) {
        Region region = Region.US;
        String path = BlizzardApiUrlBuilder.builder(region).item(itemId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        return restApiClient.get(path, params, ItemResponse.class);
    }

    public String fetchItemIconUrl(Long itemId) {
        Region region = Region.US;
        String path = BlizzardApiUrlBuilder.builder(region).media(itemId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        MediaResponse mediaResponse = restApiClient.get(path, params, MediaResponse.class);
        
        return Optional.ofNullable(mediaResponse)
                .map(MediaResponse::getAssets)
                .filter(assets -> !assets.isEmpty())
                .map(assets -> assets.get(0).getValue())
                .orElseThrow(() -> new RuntimeException("No media assets found for itemId=" + itemId));
    }
}
