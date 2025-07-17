package com.worbes.application.auction.port.in;

public interface GetAuctionItemStatsUseCase {
    GetAuctionItemStatsResult execute(GetAuctionItemStatsQuery condition);
}
