package com.worbes.auctionhousetracker.application.policy;

import java.time.Duration;
import java.time.Instant;

public interface AuctionScheduleTimePolicy {
    Duration getInterval();

    Instant getStartTime();
}
