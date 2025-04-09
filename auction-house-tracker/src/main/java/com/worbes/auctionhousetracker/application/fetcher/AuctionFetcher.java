package com.worbes.auctionhousetracker.application.fetcher;

import com.worbes.auctionhousetracker.dto.response.BlizzardAuctionListResponse;
import com.worbes.auctionhousetracker.entity.enums.RegionType;


public interface AuctionFetcher {

    BlizzardAuctionListResponse fetchAuctions(RegionType region, Long realmId);
}
