package com.worbes.application.auction.port.in;

import com.worbes.application.auction.port.out.AuctionSearchCondition;

import java.util.Map;

public interface GetActiveAuctionUseCase {
    Map<Long, Integer> groupActiveAuctionsByPrice(AuctionSearchCondition condition);
}
