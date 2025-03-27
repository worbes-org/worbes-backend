package com.worbes.auctionhousetracker.config.initializer;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.dto.response.RealmResponse;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.BlizzardApiClient;
import com.worbes.auctionhousetracker.service.RealmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@Order(3)
public class RealmDataInitializer implements DataInitializer {

    private final RealmService realmService;
    private final BlizzardApiClient blizzardApiClient;
    private final ThreadPoolTaskExecutor asyncExecutor;

    public RealmDataInitializer(
            RealmService realmService,
            BlizzardApiClient blizzardApiClient,
            @Qualifier("asyncExecutor") ThreadPoolTaskExecutor asyncExecutor
    ) {
        this.realmService = realmService;
        this.blizzardApiClient = blizzardApiClient;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public void initialize() {
        log.info("🔄 Realm 데이터 초기화 시작...");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Region region : Region.values()) {
            CompletableFuture<Void> future = fetchRealmIndexAsync(region)
                    .thenApply(response -> realmService.getMissingRealmSlugs(response, region))
                    .thenCompose(missingSlugs -> fetchAndSaveRealmsAsync(region, missingSlugs));
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("✅ Realm 데이터 초기화 완료!");
    }

    private CompletableFuture<Void> fetchAndSaveRealmsAsync(Region region, List<String> slugs) {
        if (slugs.isEmpty()) {
            log.info("[{}] 저장할 Realm 없음", region.getValue());
            return CompletableFuture.completedFuture(null);
        }

        log.info("[{}] {}개의 Realm 데이터를 API에서 조회 시작", region.getValue(), slugs.size());
        List<CompletableFuture<RealmResponse>> futures = slugs.stream()
                .map(slug -> fetchRealmAsync(region, slug))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).toList())
                .thenAccept(response -> realmService.save(region, response))
                .thenRun(() -> log.info("[{}] realm 초기화 완료", region.getValue()));
    }

    private CompletableFuture<RealmIndexResponse> fetchRealmIndexAsync(Region region) {
        return CompletableFuture.supplyAsync(() -> blizzardApiClient.fetchRealmIndex(region), asyncExecutor);
    }

    private CompletableFuture<RealmResponse> fetchRealmAsync(Region region, String realmSlug) {
        return CompletableFuture.supplyAsync(() -> blizzardApiClient.fetchRealm(region, realmSlug), asyncExecutor);
    }
}
