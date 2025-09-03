package com.worbes.application.auction.service;

import com.worbes.application.auction.port.in.CreateAuctionSnapshotUseCase;
import com.worbes.application.auction.port.out.SaveAuctionSnapshotPort;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Profile("batch")
@RequiredArgsConstructor
public class CreateAuctionSnapshotService implements CreateAuctionSnapshotUseCase {

    private final SaveAuctionSnapshotPort saveAuctionSnapshotPort;

    public int execute(RegionType region, Long realmId, Instant time) {
        if (region == null || time == null) {
            throw new IllegalArgumentException("region , time must not be null.");
        }

        return saveAuctionSnapshotPort.saveAll(region, realmId, time);
    }
}
