package com.worbes.auctionhousetracker.application.policy;

import com.worbes.auctionhousetracker.entity.enums.RegionType;

import java.util.List;

public interface AuctionScheduleRegionPolicy {
    List<RegionType> getRegions();
}
