package com.worbes.auctionhousetracker.config.initializer;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.BlizzardApiClient;
import com.worbes.auctionhousetracker.service.RealmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(3)
public class RealmDataInitializer implements DataInitializer {

    private final RealmService realmService;
    private final BlizzardApiClient blizzardApiClient;

    @Override
    public void initialize() {
        //서버 가져오기
        for (Region region : Region.values()) {
            RealmIndexResponse realmIndexResponse = blizzardApiClient.fetchRealmIndex(region);
            List<String> missingRealmSlugs = realmService.getMissingRealmSlugs(realmIndexResponse, region);
        }


        //없으면 개별 조회
    }
}
