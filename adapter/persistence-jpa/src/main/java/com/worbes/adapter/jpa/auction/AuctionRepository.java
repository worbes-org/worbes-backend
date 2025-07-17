package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.port.out.FindActiveAuctionPort;
import com.worbes.application.auction.port.out.UpdateAuctionPort;

public interface AuctionRepository extends FindActiveAuctionPort, UpdateAuctionPort {
}
