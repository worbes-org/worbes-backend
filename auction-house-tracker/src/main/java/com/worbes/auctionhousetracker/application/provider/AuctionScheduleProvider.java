package com.worbes.auctionhousetracker.application.provider;

import com.worbes.auctionhousetracker.application.schedule.AuctionSchedule;

import java.util.List;

public interface AuctionScheduleProvider {
    List<AuctionSchedule> getSchedules();
}
