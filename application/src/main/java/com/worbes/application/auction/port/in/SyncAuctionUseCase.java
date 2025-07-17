package com.worbes.application.auction.port.in;

import com.worbes.application.auction.model.Auction;

import java.util.List;

public interface SyncAuctionUseCase {
    int execute(List<Auction> auctions);
}
