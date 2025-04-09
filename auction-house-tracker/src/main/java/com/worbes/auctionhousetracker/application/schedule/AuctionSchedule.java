package com.worbes.auctionhousetracker.application.schedule;

import java.time.Duration;
import java.time.Instant;

public interface AuctionSchedule {
    Duration getInterval();

    Instant getStartTime();

    Runnable getTask();
}
