package com.worbes.auctionhousetracker.application.policy;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class DefaultAuctionScheduleTimePolicy implements AuctionScheduleTimePolicy {

    @Override
    public Duration getInterval() {
        return Duration.ofHours(1);
    }

    @Override
    public Instant getStartTime() {
        return Instant.now().minus(Duration.ofMillis(500));
    }
}
