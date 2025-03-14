package com.worbes.auctionhousetracker.runner;

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
        initializers.forEach(DataInitializer::initialize);
        log.info("🎉 모든 데이터 초기화 완료");
    }
}
