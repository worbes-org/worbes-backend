package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.oauth2.RestApiClient;
import com.worbes.auctionhousetracker.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.worbes.auctionhousetracker.utils.BlizzardApiUtils.createUrl;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final RestApiClient restApiClient;
    private final AuctionRepository repository;

    @Override
    public List<Auction> fetchAuctions(Region region) {
        return restApiClient.get(
                        createUrl(region, "/data/wow/auctions/commodities"),
                        Map.of("namespace", String.format("dynamic-%s", region.getValue())),
                        AuctionResponse.class
                )
                .getAuctions()
                .stream()
                .map(dto -> new Auction(dto, region))
                .toList();
    }

    @Override
    public List<Auction> fetchAuctions(Region region, Integer realmId) {
        return null;
    }

    @Override
    public void saveAuctions(List<Auction> auctions) {
        repository.saveAll(auctions);
    }
}
