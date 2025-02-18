package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;

import java.util.List;

public interface AuctionService {


    List<Auction> fetchCommodities(Region region);

    List<Auction> fetchAuctions(Region region, Integer realmId);

    //옥션 저장

    //옥션 수정

    //옥션 조회
}
