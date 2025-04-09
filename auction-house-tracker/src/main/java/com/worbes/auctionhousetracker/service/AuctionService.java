package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.mapper.AuctionUpdateCommand;

public interface AuctionService {

    void updateAuctions(AuctionUpdateCommand command);
}
