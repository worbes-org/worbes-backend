package com.worbes.auctionhousetracker.application.fetcher;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionFetcherImpl implements AuctionFetcher {

    private final RestApiClient restApiClient;

    @Override
    public AuctionResponse fetchCommodities(Region region) {
        return null;
    }

    @Override
    public AuctionResponse fetchAuctions(Region region, Realm realm) {
        return null;
    }

}
