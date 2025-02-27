package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;

public interface RealmService {

    RealmIndexResponse fetchRealmIndex(Region region);

    Realm fetchRealm(Region region, String slug);
}
