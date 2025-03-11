package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.ItemResponse;
import com.worbes.auctionhousetracker.dto.response.MediaResponse;
import com.worbes.auctionhousetracker.entity.Item;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import com.worbes.auctionhousetracker.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.STATIC;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final RestApiClient restApiClient;
    private final ItemRepository itemRepository;
    private final ThreadPoolTaskScheduler taskScheduler;

    public ItemServiceImpl(RestApiClient restApiClient,
                           ItemRepository itemRepository,
                           @Qualifier("taskScheduler") ThreadPoolTaskScheduler taskScheduler) {
        this.restApiClient = restApiClient;
        this.itemRepository = itemRepository;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Override
    public Item getItem(Long id) {
        log.info("🔍 아이템 조회 시작 (ID: {})", id);
        Optional<Item> optional = itemRepository.findById(id);

        if (optional.isPresent()) {
            log.info("✅ DB에서 아이템 조회 성공 (ID: {})", id);
            return optional.get();
        }

        log.info("⚠️ DB에서 아이템을 찾을 수 없음. API로 조회 시작 (ID: {})", id);
        Item item = collectItemWithMedia(id);
        saveItem(item);
        return item;
    }

    @Override
    public Item collectItemWithMedia(Long itemId) {
        log.info("🔄 아이템 정보 수집 시작 (ID: {})", itemId);

        CompletableFuture<ItemResponse> itemFuture = CompletableFuture
                .supplyAsync(() -> fetchItemData(itemId), taskScheduler);

        CompletableFuture<String> mediaFuture = CompletableFuture
                .supplyAsync(() -> fetchItemIconUrl(itemId), taskScheduler);

        ItemResponse itemResponse = itemFuture.join();
        String iconUrl = mediaFuture.join();

        Item item = Item.from(itemResponse, iconUrl);
        log.info("✅ 아이템 정보 수집 완료 (ID: {}, 이름: {})", itemId, item.getName());

        return item;
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
                .orElseThrow(() -> {
                    log.error("❌ 아이템 미디어 정보를 찾을 수 없음 (ID: {})", itemId);
                    return new RuntimeException("No media assets found for itemId=" + itemId);
                });
    }
}
