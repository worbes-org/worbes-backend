package com.worbes.auctionhousetracker.infrastructure.rest;

import com.worbes.auctionhousetracker.dto.response.*;
import com.worbes.auctionhousetracker.entity.enums.RegionType;

public interface BlizzardApiClient {
    ItemClassesIndexResponse fetchItemClassesIndex();

    ItemClassResponse fetchItemClass(Long itemClassId);

    ItemSubclassResponse fetchItemSubclass(Long itemClassId, Long subclassId);

    RealmIndexResponse fetchRealmIndex(RegionType region);

    RealmResponse fetchRealm(RegionType region, String slug);
}
