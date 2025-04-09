package com.worbes.auctionhousetracker.application.schedule;


import com.worbes.auctionhousetracker.application.provider.AuctionScheduleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionScheduleProvider scheduleProvider;
    private final ThreadPoolTaskScheduler taskScheduler;

    @EventListener(ApplicationReadyEvent.class)
    public void startScheduledTask() {
        List<AuctionSchedule> schedules = scheduleProvider.getSchedules();
        log.info("스케줄링 시작: 총 {}개의 스케줄이 등록되었습니다.", schedules.size());

        for (AuctionSchedule auctionSchedule : schedules) {
            log.info("스케줄 등록 중: 시작 시간={}, 반복 주기(ms)={}, 작업={}",
                    auctionSchedule.getStartTime(),
                    auctionSchedule.getInterval(),
                    auctionSchedule.getTask().getClass().getName()
            );

            taskScheduler.scheduleAtFixedRate(
                    () -> auctionSchedule.getTask().run(),
                    auctionSchedule.getStartTime(),
                    auctionSchedule.getInterval()
            );
        }
        log.info("모든 스케줄이 정상적으로 등록되었습니다.");
    }
}
