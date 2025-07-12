package com.worbes.application.auction.port.in;

import com.worbes.application.auction.model.AuctionTrend;
import com.worbes.application.auction.port.out.AuctionTrendSearchCondition;

public interface GetAuctionTrendUseCase {
    AuctionTrend getHourlyTrend(AuctionTrendSearchCondition condition);
}
