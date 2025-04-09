package com.worbes.auctionhousetracker.application.policy;

import com.worbes.auctionhousetracker.entity.enums.RegionType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StaticRegionSelectionPolicy implements AuctionScheduleRegionPolicy {
    @Override
    public List<RegionType> getRegions() {
        return List.of(RegionType.KR);
    }
}
