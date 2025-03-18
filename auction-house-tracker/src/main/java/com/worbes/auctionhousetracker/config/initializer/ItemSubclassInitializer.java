package com.worbes.auctionhousetracker.config.initializer;

import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.infrastructure.rest.BlizzardApiClient;
import com.worbes.auctionhousetracker.service.ItemSubclassServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@Order(2)
public class ItemSubclassInitializer implements DataInitializer {

    private final ItemSubclassServiceImpl itemSubclassService;
    private final BlizzardApiClient blizzardApiClient;
    private final ThreadPoolTaskExecutor asyncExecutor;

    public ItemSubclassInitializer(
            ItemSubclassServiceImpl itemSubclassService,
            BlizzardApiClient blizzardApiClient,
            @Qualifier("asyncExecutor") ThreadPoolTaskExecutor asyncExecutor) {
        this.itemSubclassService = itemSubclassService;
        this.blizzardApiClient = blizzardApiClient;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public void initialize() {
        Map<Long, Set<Long>> missingSubclasses = itemSubclassService.getMissingItemSubclasses();
        if (missingSubclasses.isEmpty()) {
            log.info("✅ 모든 서브클래스가 정상적으로 존재합니다.");
            return;
        }
        log.warn("🚨 누락된 서브클래스 목록: {}", missingSubclasses);
        List<CompletableFuture<Void>> futures = missingSubclasses
                .entrySet()
                .stream()
                .map(entry -> fetchAndSaveItemSubclassesAsync(entry.getKey(), entry.getValue()))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private CompletableFuture<Void> fetchAndSaveItemSubclassesAsync(Long itemClassId, Set<Long> subclassIds) {
        List<CompletableFuture<ItemSubclassResponse>> futures = subclassIds.stream()
                .map(subclassId -> fetchItemSubclassAsync(itemClassId, subclassId))
                .toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).toList())
                .thenAccept(responses -> itemSubclassService.saveRequiredSubclass(responses, itemClassId))
                .thenRun(() -> log.info("✅ [{}] - 서브 클래스 저장 완료", itemClassId));
    }

    private CompletableFuture<ItemSubclassResponse> fetchItemSubclassAsync(Long itemClassId, Long subclassId) {
        return CompletableFuture.supplyAsync(() -> blizzardApiClient.fetchItemSubclass(itemClassId, subclassId), asyncExecutor);
    }
}
