package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.oauth2.RestApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.worbes.auctionhousetracker.config.properties.RestClientConfigProperties.*;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final RestApiClient restApiClient;

    @Override
    public List<Auction> fetchAuctions(Region region) {
        String base = String.format(BASE_URL, region.getValue());
        Map<String, String> params = Map.of(NAMESPACE_KEY, String.format(NAMESPACE_DYNAMIC, region.getValue()));
        return restApiClient.get(base.concat(COMMODITIES_URL), params, AuctionResponse.class)
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
