package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.ItemResponse;
import com.worbes.auctionhousetracker.entity.Item;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.STATIC;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final RestApiClient restApiClient;

    @Override
    public Item fetchItem(Long id) {
        Region region = Region.US;
        String path = BlizzardApiUrlBuilder.builder(region).item(id).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        return Item.from(restApiClient.get(path, params, ItemResponse.class));
    }
}
