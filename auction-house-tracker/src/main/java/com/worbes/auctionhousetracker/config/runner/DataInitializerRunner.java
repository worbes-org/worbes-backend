package com.worbes.auctionhousetracker.config.runner;

import com.worbes.auctionhousetracker.config.initializer.DataInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializerRunner implements CommandLineRunner {

    private final List<DataInitializer> initializers;

    @Override
    public void run(String... args) {
        log.info("🚀 데이터 초기화 시작");
        log.info("▶ 실행할 이니셜라이저 목록: {}",
                initializers.stream().map(init -> init.getClass().getSimpleName()).toList());

        long startTime = System.currentTimeMillis(); // ⏱️ 전체 실행 시간 측정

        initializers.forEach(initializer -> {
            long initStartTime = System.currentTimeMillis(); // ⏱️ 개별 초기화 시작 시간
            log.info("🔄 {} 초기화 시작...", initializer.getClass().getSimpleName());
            initializer.initialize();
            long initEndTime = System.currentTimeMillis();
            log.info("✅ {} 초기화 완료 ({}ms)",
                    initializer.getClass().getSimpleName(), (initEndTime - initStartTime));
        });

        long endTime = System.currentTimeMillis();
        log.info("🎉 모든 데이터 초기화 완료! 총 실행 시간: {}ms", (endTime - startTime));
    }
}
