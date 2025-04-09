package com.worbes.auctionhousetracker.application.fetcher;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.dto.response.RealmResponse;
import com.worbes.auctionhousetracker.entity.enums.RegionType;

public interface RealmFetcher {
    RealmIndexResponse fetchRealmIndex(RegionType region);

    RealmResponse fetchRealm(RegionType region, String slug);
}
