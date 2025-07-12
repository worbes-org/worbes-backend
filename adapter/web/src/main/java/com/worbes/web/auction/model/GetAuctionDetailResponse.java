package com.worbes.web.auction.model;

import com.worbes.application.auction.model.AuctionTrendPoint;

import java.util.List;
import java.util.Map;

public record GetAuctionDetailResponse(
        ItemResponse item,
        Map<Long, Integer> priceGroup,
        List<AuctionTrendPoint> trend
) {
}
