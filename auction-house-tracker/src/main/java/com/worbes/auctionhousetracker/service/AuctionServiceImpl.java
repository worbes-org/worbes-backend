package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.oauth2.RestApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final RestApiClient restApiClient;

    @Override
    public List<Auction> fetchAuctions(Region region) {
        String base = String.format("https://%s.api.blizzard.com", region.getValue());
        String path = "/data/wow/auctions/commodities";
        Map<String, String> params = Map.of("namespace", String.format("dynamic-%s", region.getValue()));
        return restApiClient.get(base.concat(path), params, AuctionResponse.class)
                .getAuctions()
                .stream()
                .map(Auction::new)
                .toList();
    }

    @Override
    public List<Auction> fetchAuctions(Region region, Integer realmId) {
        return null;
    }
}
