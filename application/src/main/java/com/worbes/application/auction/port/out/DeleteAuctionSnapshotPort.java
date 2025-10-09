package com.worbes.application.auction.port.out;

public interface DeleteAuctionSnapshotPort {
    long deleteOlderThanOneMonth();
}
