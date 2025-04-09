package com.worbes.auctionhousetracker.infrastructure.rest;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.*;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.DYNAMIC;
import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.STATIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlizzardApiClientImpl implements BlizzardApiClient {

    private final RestApiClient restApiClient;

    @Override
    public ItemClassesIndexResponse fetchItemClassesIndex() {
        RegionType region = RegionType.US;
        String path = BlizzardApiUrlBuilder.builder(region).itemClassIndex().build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        return restApiClient.get(path, params, ItemClassesIndexResponse.class);
    }

    @Override
    public ItemClassResponse fetchItemClass(Long itemClassId) {
        RegionType region = RegionType.US;
        String url = BlizzardApiUrlBuilder.builder(region).itemClass(itemClassId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        return restApiClient.get(url, params, ItemClassResponse.class);
    }

    @Override
    public ItemSubclassResponse fetchItemSubclass(Long itemClassId, Long subclassId) {
        RegionType region = RegionType.US;
        String url = BlizzardApiUrlBuilder.builder(region).itemSubclass(itemClassId, subclassId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        return restApiClient.get(url, params, ItemSubclassResponse.class);
    }

    @Override
    public RealmIndexResponse fetchRealmIndex(RegionType region) {
        return restApiClient.get(
                BlizzardApiUrlBuilder.builder(region).realmIndex().build(),
                BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build(),
                RealmIndexResponse.class
        );
    }

    @Override
    public RealmResponse fetchRealm(RegionType region, String slug) {
        return restApiClient.get(
                BlizzardApiUrlBuilder.builder(region).realm(slug).build(),
                BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build(),
                RealmResponse.class
        );
    }
}
