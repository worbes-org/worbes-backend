package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.enums.Region;

public interface AuctionService {


    void updateAuctions(AuctionResponse auctionResponse, Region region);

    void updateAuctions(AuctionResponse auctionResponse, Region region, Long realmId);
}
