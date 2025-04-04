package com.worbes.auctionhousetracker.infrastructure.rest;

import com.worbes.auctionhousetracker.dto.response.*;
import com.worbes.auctionhousetracker.entity.enums.Region;

import java.util.concurrent.CompletableFuture;

public interface BlizzardApiClient {
    AuctionResponse fetchCommodities(Region region);

    AuctionResponse fetchAuctions(Region region, Long realmId);

    ItemClassesIndexResponse fetchItemClassesIndex();

    ItemClassResponse fetchItemClass(Long itemClassId);

    ItemSubclassResponse fetchItemSubclass(Long itemClassId, Long subclassId);

    ItemResponse fetchItem(Long itemId);

    CompletableFuture<ItemMediaResponse> fetchItemWithMediaAsync(Long itemId);

    MediaResponse fetchItemMedia(Long itemId);

    RealmIndexResponse fetchRealmIndex(Region region);

    RealmResponse fetchRealm(Region region, String slug);
}
