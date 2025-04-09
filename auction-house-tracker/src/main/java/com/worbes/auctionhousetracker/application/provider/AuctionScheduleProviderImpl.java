package com.worbes.auctionhousetracker.application.provider;

import com.worbes.auctionhousetracker.application.policy.AuctionScheduleRegionPolicy;
import com.worbes.auctionhousetracker.application.policy.AuctionScheduleTimePolicy;
import com.worbes.auctionhousetracker.application.schedule.AuctionSchedule;
import com.worbes.auctionhousetracker.application.schedule.PerConnectedRealmSchedule;
import com.worbes.auctionhousetracker.application.schedule.PerRegionSchedule;
import com.worbes.auctionhousetracker.application.sychronizer.AuctionSynchronizer;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import com.worbes.auctionhousetracker.service.RealmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuctionScheduleProviderImpl implements AuctionScheduleProvider {

    private final AuctionScheduleTimePolicy timePolicy;
    private final AuctionScheduleRegionPolicy regionPolicy;
    private final RealmService realmService;
    private final AuctionSynchronizer synchronizer;

    @Override
    public List<AuctionSchedule> getSchedules() {
        return regionPolicy.getRegions().stream()
                .flatMap(this::createSchedulesForRegion)
                .toList();
    }

    private Stream<AuctionSchedule> createSchedulesForRegion(RegionType region) {
        AuctionSchedule commodities = createCommoditiesSchedule(region);
//        List<AuctionSchedule> realmSchedules = createRealmSchedules(region);
        List<AuctionSchedule> realmSchedules = List.of();

        return Stream.concat(Stream.of(commodities), realmSchedules.stream());
    }

    private AuctionSchedule createCommoditiesSchedule(RegionType region) {
        return new PerRegionSchedule(region, timePolicy, synchronizer);
    }

    private List<AuctionSchedule> createRealmSchedules(RegionType region) {
        List<Long> realmIds = realmService.getConnectedRealmIds(region);
        return realmIds.stream()
                .map(realmId -> new PerConnectedRealmSchedule(region, realmId, timePolicy, synchronizer))
                .map(schedule -> (AuctionSchedule) schedule)
                .toList();
    }
}
