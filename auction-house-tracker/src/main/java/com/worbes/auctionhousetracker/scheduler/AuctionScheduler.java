package com.worbes.auctionhousetracker.scheduler;


import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionService auctionService;

    @Scheduled(fixedRate = 60 * 60 * 1000) // 1시간 마다 실행
    public void collectCommoditiesKR() {
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
