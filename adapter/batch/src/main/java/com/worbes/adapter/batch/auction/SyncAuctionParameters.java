package com.worbes.adapter.batch.auction;

import lombok.Getter;

@Getter
public enum SyncAuctionParameters {

    REALM_ID("realmId"),
    REGION("region"),
    AUCTION_COUNT("auctionCount"),
    AUCTION_DATE("auctionDate"),
    AUCTION_SNAPSHOT("snapshot");

    private final String key;

    SyncAuctionParameters(String key) {
        this.key = key;
    }
}
