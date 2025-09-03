package com.worbes.application.auction.port.in;

public interface GetAuctionDetailUseCase {
    GetAuctionDetailResult execute(GetAuctionDetailQuery condition);
}
