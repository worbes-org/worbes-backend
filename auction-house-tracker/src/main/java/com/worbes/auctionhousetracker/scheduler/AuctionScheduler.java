package com.worbes.auctionhousetracker.scheduler;


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

    private final ThreadPoolTaskScheduler taskScheduler;

    @EventListener(ApplicationReadyEvent.class) // 애플리케이션 준비 완료 후 실행
    public void startScheduledTask() {
        taskScheduler.scheduleAtFixedRate(this::collectAuctionData, Duration.ofHours(1));
    }

    public void collectAuctionData() {

    }
}
