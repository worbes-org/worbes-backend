package com.worbes.application.auction.service;

import com.worbes.application.auction.port.in.DeleteAuctionSnapshotUseCase;
import com.worbes.application.auction.port.out.DeleteAuctionSnapshotPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("batch")
@RequiredArgsConstructor
public class DeleteAuctionSnapshotService implements DeleteAuctionSnapshotUseCase {

    private final DeleteAuctionSnapshotPort deleteAuctionSnapshotPort;

    @Override
    public long execute() {
        return deleteAuctionSnapshotPort.deleteOlderThanOneMonth();
    }
}
