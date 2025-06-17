package com.worbes.adapter.blizzard.data.auction;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.client.BlizzardApiException;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.application.auction.port.out.AuctionFetcher;
import com.worbes.application.auction.port.out.FetchAuctionResult;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuctionFetcherImpl implements AuctionFetcher {

    private final BlizzardApiClient apiClient;
    private final AuctionListResponseMapper resultMapper;
    private final BlizzardApiUriFactory uriFactory;

    @Override
    public List<FetchAuctionResult> fetch(RegionType region, Long realmId) {
        URI uri = Optional.ofNullable(realmId)
                .map(id -> uriFactory.auctionUri(region, id))
                .orElseGet(() -> uriFactory.commodityUri(region));

        try {
            AuctionListResponse result = apiClient.fetch(uri, AuctionListResponse.class);
            return result.getAuctions().stream()
                    .map(response -> resultMapper.toDto(region, realmId, response))
                    .toList();
        } catch (BlizzardApiException e) {
            return null;
        }
    }
}
