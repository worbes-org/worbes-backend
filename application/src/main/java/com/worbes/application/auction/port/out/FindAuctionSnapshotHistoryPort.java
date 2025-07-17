package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.AuctionSnapshot;
import com.worbes.application.auction.port.in.GetAuctionItemTrendQuery;

import java.util.List;

public interface FindAuctionSnapshotHistoryPort {
    List<AuctionSnapshot> findHistory(GetAuctionItemTrendQuery query);
}
