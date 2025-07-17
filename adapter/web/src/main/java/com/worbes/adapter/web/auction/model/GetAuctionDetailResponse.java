package com.worbes.adapter.web.auction.model;

import com.worbes.application.auction.port.in.GetAuctionItemStatsResult;
import com.worbes.application.auction.port.in.GetAuctionItemTrendResult;

public record GetAuctionDetailResponse(
        GetAuctionItemStatsResult auctionItemStatsResult,
        GetAuctionItemTrendResult auctionItemTrendResult
) {
}
