package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.AuctionTrendPoint;
import com.worbes.application.auction.port.in.GetAuctionTrendQuery;

import java.util.List;

public interface FindAuctionTrendPort {
    List<AuctionTrendPoint> findTrendsBy(GetAuctionTrendQuery query);
}
