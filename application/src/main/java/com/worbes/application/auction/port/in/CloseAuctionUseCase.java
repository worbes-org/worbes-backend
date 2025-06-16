package com.worbes.application.auction.port.in;

public interface CloseAuctionUseCase {
    Long closeAuctions(CloseAuctionCommand command);
}
