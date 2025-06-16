package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.FetchAuctionCommand;
import com.worbes.application.auction.port.in.FetchAuctionUseCase;
import com.worbes.application.auction.port.out.AuctionFetcher;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FetchAuctionService implements FetchAuctionUseCase {

    private final AuctionFetcher auctionFetcher;
    private final AuctionFactory auctionFactory;

    @Override
    public List<Auction> fetchAuctions(FetchAuctionCommand command) {
        RegionType regionType = command.region();
        Long realmId = command.realmId();

        return auctionFetcher.fetch(regionType, realmId).stream()
                .map(auctionFactory::create)
                .toList();
    }
}

