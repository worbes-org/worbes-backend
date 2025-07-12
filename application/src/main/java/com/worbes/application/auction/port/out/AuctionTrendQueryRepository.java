package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.AuctionTrendPoint;

import java.util.List;

public interface AuctionTrendQueryRepository {
    List<AuctionTrendPoint> findHourlyTrend(AuctionTrendSearchCondition condition);
}
