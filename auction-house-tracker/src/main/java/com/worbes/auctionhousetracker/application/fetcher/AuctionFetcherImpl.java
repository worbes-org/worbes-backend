package com.worbes.auctionhousetracker.application.fetcher;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.BlizzardAuctionListResponse;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.DYNAMIC;

@Component
@RequiredArgsConstructor
public class AuctionFetcherImpl implements AuctionFetcher {

    private final RestApiClient restApiClient;

    @Override
    public BlizzardAuctionListResponse fetchAuctions(RegionType region, Long realmId) {
        if (realmId == null) return fetchCommodities(region);
        return restApiClient.get(
                BlizzardApiUrlBuilder.builder(region).auctions(realmId).build(),
                BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build(),
                BlizzardAuctionListResponse.class);
    }

    private BlizzardAuctionListResponse fetchCommodities(RegionType region) {
        return restApiClient.get(
                BlizzardApiUrlBuilder.builder(region).commodities().build(),
                BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build(),
                BlizzardAuctionListResponse.class
        );
    }
}
