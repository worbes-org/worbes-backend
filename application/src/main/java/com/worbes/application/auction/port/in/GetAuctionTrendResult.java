package com.worbes.application.auction.port.in;

import com.worbes.application.auction.model.AuctionTrendPoint;

import java.util.List;

public record GetAuctionTrendResult(
        Long averageLowestPrice,
        Long medianLowestPrice,
        List<AuctionTrendPoint> trendPoints
) {
}
