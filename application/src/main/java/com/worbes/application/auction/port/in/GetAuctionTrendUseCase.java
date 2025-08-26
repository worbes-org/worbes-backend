package com.worbes.application.auction.port.in;

public interface GetAuctionTrendUseCase {
    GetAuctionTrendResult execute(GetAuctionTrendQuery input);
}
