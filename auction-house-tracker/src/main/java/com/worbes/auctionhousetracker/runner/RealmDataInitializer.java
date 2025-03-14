package com.worbes.auctionhousetracker.runner;

import com.worbes.auctionhousetracker.service.RealmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(3)
public class RealmDataInitializer implements CommandLineRunner {

    private final RealmService realmService;

    @Override
    public void run(String... args) {
//        log.info("▶️ 서버 데이터 초기화 시작");
//        if (realmService.isRealmInitialized()) {
//            log.info("✅ 서버 데이터가 이미 초기화되어 있습니다.");
//            return;
//        }
//        List<CompletableFuture<Void>> futures = Stream.of(Region.values())
//                .map(realmService::fetchAndSaveRealms)
//                .toList();
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                .join();
//        log.info("🎉 모든 서버 데이터 초기화가 완료되었습니다.");
    }
}
