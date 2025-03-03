package com.worbes.auctionhousetracker.scheduler;


import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.service.AuctionService;
import com.worbes.auctionhousetracker.service.RealmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionService auctionService;
    private final RealmService realmService;
    private final ThreadPoolTaskScheduler taskScheduler;

    @EventListener(ApplicationReadyEvent.class) // 애플리케이션 준비 완료 후 실행
    public void startScheduledTask() {
        log.info("⏳ 스케줄러 시작...");
        taskScheduler.scheduleAtFixedRate(this::collectAuctionData, Duration.ofHours(1));
    }

    public void collectAuctionData() {
        log.info("⏳ Fetching auction data...");
        try {
            Region region = Region.KR;

            //region 전체 공통 경매 업데이트
            auctionService.updateAuctions(auctionService.fetchCommodities(region), region);

            //region - connected realm 경매 업데이트
            realmService.getConnectedRealmIdsByRegion(region).forEach(
                    id -> auctionService.updateAuctions(auctionService.fetchAuctions(region, id), region, id)
            );

            log.info("✅ Auction data updated successfully.");
        } catch (Exception e) {
            log.error("❌ Failed to update auction data", e);
        }
    }
}
