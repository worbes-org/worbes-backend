package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.FetchAuctionUseCase;
import com.worbes.application.auction.port.out.FetchAuctionApiPort;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("batch")
@RequiredArgsConstructor
public class FetchAuctionApiService implements FetchAuctionUseCase {

    private final FetchAuctionApiPort fetchAuctionApiPort;

    @Override
    public List<Auction> execute(RegionType region, Long realmId) {
        if (region == null) {
            throw new IllegalArgumentException("Region must not be null");
        }

        return fetchAuctionApiPort.fetch(region, realmId).stream()
                .toList();
    }
}

