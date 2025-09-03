package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.AuctionSnapshot;
import com.worbes.application.auction.port.in.SearchAuctionSummaryQuery;

import java.util.List;

public interface FindAuctionSnapshotPort {
    List<AuctionSnapshot> findBy(SearchAuctionSummaryQuery query);
}
