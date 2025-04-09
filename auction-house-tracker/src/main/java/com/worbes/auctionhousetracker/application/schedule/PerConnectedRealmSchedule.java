package com.worbes.auctionhousetracker.application.schedule;

import com.worbes.auctionhousetracker.application.policy.AuctionScheduleTimePolicy;
import com.worbes.auctionhousetracker.application.sychronizer.AuctionSynchronizer;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
public class PerConnectedRealmSchedule implements AuctionSchedule {

    private final RegionType region;
    private final Long realmId;
    private final AuctionScheduleTimePolicy policy;
    private final AuctionSynchronizer synchronizer;

    @Override
    public Duration getInterval() {
        return policy.getInterval();
    }

    @Override
    public Instant getStartTime() {
        return policy.getStartTime();
    }

    @Override
    public Runnable getTask() {
        return () -> synchronizer.synchronize(region, realmId);
    }
}
