package com.worbes.auctionhousetracker.application.fetcher;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.dto.response.RealmResponse;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.DYNAMIC;

@Component
@RequiredArgsConstructor
public class RealmFetcherImpl implements RealmFetcher {

    private final RestApiClient restApiClient;

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
