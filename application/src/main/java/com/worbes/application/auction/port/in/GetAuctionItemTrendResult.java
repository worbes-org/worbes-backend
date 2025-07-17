package com.worbes.application.auction.port.in;

import com.worbes.application.auction.model.AuctionTrendPoint;

import java.util.List;

public record GetAuctionItemTrendResult(
        Long averageLowestPrice,
        Long averageMedianPrice,
        List<AuctionTrendPoint> trendPoints
) {
}
