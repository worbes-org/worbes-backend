package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.FetchAuctionUseCase;
import com.worbes.application.auction.port.out.AuctionApiFetcher;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionApiFetchService implements FetchAuctionUseCase {

    private final AuctionApiFetcher auctionApiFetcher;

    @Override
    public List<Auction> fetch(RegionType region, Long realmId) {
        if (region == null || realmId == null) {
            throw new IllegalArgumentException("Region and Realm ID must not be null");
        }

        return auctionApiFetcher.fetch(region, realmId).stream()
                .toList();
    }

    @Override
    public List<Auction> fetch(RegionType region) {
        if (region == null) {
            throw new IllegalArgumentException("Region must not be null");
        }

        return auctionApiFetcher.fetch(region).stream()
                .toList();
    }
}

