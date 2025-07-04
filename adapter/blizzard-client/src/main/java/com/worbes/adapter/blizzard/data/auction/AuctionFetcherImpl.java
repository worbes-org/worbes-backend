package com.worbes.adapter.blizzard.data.auction;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.application.auction.port.out.AuctionFetcher;
import com.worbes.application.auction.port.out.FetchAuctionResult;
import com.worbes.application.auction.port.out.FetchCommodityResult;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuctionFetcherImpl implements AuctionFetcher {

    private final BlizzardApiClient apiClient;
    private final AuctionListResponseMapper auctionListResponseMapper;
    private final CommodityListResponseMapper commodityListResponseMapper;
    private final BlizzardApiUriFactory uriFactory;

    @Override
    public List<FetchAuctionResult> fetchAuctions(RegionType region, Long realmId) {
        URI uri = uriFactory.auctionUri(region, realmId);
        AuctionListResponse result = apiClient.fetch(uri, AuctionListResponse.class);

        return result.getAuctions().stream()
                .map(response -> auctionListResponseMapper.toDto(region, realmId, response))
                .toList();
    }

    @Override
    public List<FetchCommodityResult> fetchCommodities(RegionType region) {
        URI uri = uriFactory.commodityUri(region);
        CommodityListResponse result = apiClient.fetch(uri, CommodityListResponse.class);

        return result.getAuctions().stream()
                .map(response -> commodityListResponseMapper.toDto(region, response))
                .toList();
    }
}
