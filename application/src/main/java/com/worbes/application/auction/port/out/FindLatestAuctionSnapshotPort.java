package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.AuctionSnapshot;
import com.worbes.application.auction.port.in.SearchAuctionItemQuery;

import java.util.List;

public interface FindLatestAuctionSnapshotPort {
    List<AuctionSnapshot> findLatest(SearchAuctionItemQuery query);
}
