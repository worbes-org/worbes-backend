package com.worbes.auctionhousetracker.scheduler;


import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionService auctionService;
    private final ThreadPoolTaskScheduler taskScheduler;

    @EventListener(ApplicationReadyEvent.class) // 애플리케이션 준비 완료 후 실행
    public void startScheduledTask() {
        log.info("⏳ 스케줄러 시작...");
        taskScheduler.scheduleAtFixedRate(this::collectAuctionData, Duration.ofHours(1));
    }

    public void collectAuctionData() {
        log.info("⏳ Fetching auction data...");
        try {
            Region region = Region.KR; // 지역 설정 (필요하면 여러 개 지원 가능)
            List<Auction> newAuctions = auctionService.fetchCommodities(region);
            auctionService.updateAuctions(newAuctions, region);
            log.info("✅ Auction data updated successfully.");
        } catch (Exception e) {
            log.error("❌ Failed to update auction data", e);
        }
    }
}
