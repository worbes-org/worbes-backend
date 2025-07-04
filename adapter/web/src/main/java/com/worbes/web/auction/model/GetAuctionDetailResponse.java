package com.worbes.web.auction.model;

import com.worbes.application.auction.model.AuctionStatsSnapshot;

import java.util.List;
import java.util.Map;

public record GetAuctionDetailResponse(
        ItemResponse item,
        Map<Long, Long> available,
        List<AuctionStatsSnapshot> statsSnapshots
) {
}
