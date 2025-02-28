package com.worbes.auctionhousetracker.runner;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.service.RealmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Slf4j
@Component
@Order(3)
public class RealmDataInitializer implements CommandLineRunner {

    private final RealmService realmService;
    private final ThreadPoolTaskExecutor taskExecutor;

    public RealmDataInitializer(RealmService realmService, @Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.realmService = realmService;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void run(String... args) {
        log.info("▶️ 서버 데이터 초기화 시작");

        if (isDataAlreadyInitialized()) {
            log.info("✅ 서버 데이터가 이미 초기화되어 있습니다.");
            return;
        }
        List<CompletableFuture<Void>> futures = Stream.of(Region.values())
                .map(this::fetchAndSaveRealms)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .join();

        log.info("🎉 모든 서버 데이터 초기화가 완료되었습니다.");
    }

    private boolean isDataAlreadyInitialized() {
        long count = realmService.count();
        log.info("현재 저장된 서버 수: {}", count);
        return count > 0;
    }

    private CompletableFuture<Void> fetchAndSaveRealms(Region region) {
        return fetchRealmIndexAsync(region)
                .thenApply(realmIndex -> fetchRealmsAsync(region, realmIndex))
                .thenApply(futures -> futures.stream().map(CompletableFuture::join).toList())
                .thenAccept(realmService::saveAll)
                .thenRun(() -> log.info("✅ [{}] - 모든 서버 데이터 저장 완료", region.name()));
    }

    private CompletableFuture<RealmIndexResponse> fetchRealmIndexAsync(Region region) {
        return CompletableFuture.supplyAsync(() -> realmService.fetchRealmIndex(region), taskExecutor);
    }

    private List<CompletableFuture<Realm>> fetchRealmsAsync(Region region, RealmIndexResponse realmIndex) {
        return realmIndex.getRealms().stream()
                .map(realmReference -> CompletableFuture.supplyAsync(() ->
                        realmService.fetchRealm(region, realmReference.getSlug()), taskExecutor))
                .toList();
    }
}
