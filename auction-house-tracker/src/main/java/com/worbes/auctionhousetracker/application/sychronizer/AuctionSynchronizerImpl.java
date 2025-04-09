package com.worbes.auctionhousetracker.application.sychronizer;

import com.worbes.auctionhousetracker.application.fetcher.AuctionFetcher;
import com.worbes.auctionhousetracker.application.resolver.ItemResolverImpl;
import com.worbes.auctionhousetracker.dto.mapper.AuctionUpdateCommandMapper;
import com.worbes.auctionhousetracker.dto.response.BlizzardAuctionListResponse;
import com.worbes.auctionhousetracker.dto.response.BlizzardAuctionResponse;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import com.worbes.auctionhousetracker.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuctionSynchronizerImpl implements AuctionSynchronizer {

    private final AuctionFetcher auctionFetcher;
    private final AuctionService auctionService;
    private final ItemResolverImpl itemResolver;
    private final AuctionUpdateCommandMapper mapper;

    @Override
    public void synchronize(RegionType region) {
        synchronize(region, null);
    }

    @Override
    public void synchronize(RegionType region, Long realmId) {
        BlizzardAuctionListResponse fullResponse = auctionFetcher.fetchAuctions(region, realmId);
        List<BlizzardAuctionResponse> auctions = fullResponse.getAuctions();
        int chunkSize = 500;
        for (int i = 0; i < auctions.size(); i += chunkSize) {
            List<BlizzardAuctionResponse> chunk = auctions.subList(i, Math.min(i + chunkSize, auctions.size()));
            itemResolver.resolveItems(extractItemIds(chunk));
            auctionService.updateAuctions(mapper.toCommand(chunk, region, realmId));
        }
    }

    private List<Long> extractItemIds(List<BlizzardAuctionResponse> auctions) {
        return auctions.stream()
                .map(BlizzardAuctionResponse::getItemId)
                .distinct()
                .collect(Collectors.toList());
    }
}
