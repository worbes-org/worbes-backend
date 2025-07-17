package com.worbes.adapter.blizzard.data.auction;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.adapter.blizzard.data.shared.BlizzardResponseValidator;
import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.out.FetchAuctionApiPort;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FetchAuctionApiAdapter implements FetchAuctionApiPort {

    private final BlizzardApiClient apiClient;
    private final AuctionListResponseMapper auctionListResponseMapper;
    private final CommodityListResponseMapper commodityListResponseMapper;
    private final BlizzardApiUriFactory uriFactory;
    private final BlizzardResponseValidator validator;

    @Override
    public List<Auction> fetch(RegionType region, Long realmId) {
        if (realmId == null) {
            return fetchCommodityApi(region);
        }
        return fetchAuctionApi(region, realmId);
    }

    private List<Auction> fetchAuctionApi(RegionType region, Long realmId) {
        URI uri = uriFactory.auctionUri(region, realmId);
        AuctionListResponse result = apiClient.fetch(uri, AuctionListResponse.class);

        return result.auctions().stream()
                .map(validator::validate)
                .map(response -> auctionListResponseMapper.toDomain(region, realmId, response))
                .toList();
    }

    private List<Auction> fetchCommodityApi(RegionType region) {
        URI uri = uriFactory.commodityUri(region);
        CommodityListResponse result = apiClient.fetch(uri, CommodityListResponse.class);

        return result.auctions().stream()
                .map(validator::validate)
                .map(response -> commodityListResponseMapper.toDomain(region, response))
                .toList();
    }
}
