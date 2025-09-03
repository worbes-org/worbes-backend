package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.GetAuctionDetailQuery;

import java.util.List;

public interface FindAuctionPort {
    List<Auction> findBy(GetAuctionDetailQuery query);
}
