package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.Auction;

import java.util.List;

public interface CreateAuctionRepository {
    int saveAllIgnoreConflict(List<Auction> auctions);
}
