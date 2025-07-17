package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.GetAuctionItemStatsQuery;

import java.util.List;

public interface FindActiveAuctionPort {
    List<Auction> findActive(GetAuctionItemStatsQuery condition);
}
