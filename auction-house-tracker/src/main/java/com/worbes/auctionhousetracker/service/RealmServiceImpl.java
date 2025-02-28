package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.dto.response.RealmResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import com.worbes.auctionhousetracker.repository.RealmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.DYNAMIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealmServiceImpl implements RealmService {

    private final RealmRepository realmRepository;
    private final RestApiClient restApiClient;

    public static Long extractIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL must not be null or empty");
        }

        String[] parts = url.split("\\?")[0].split("/");
        String lastPart = parts[parts.length - 1];

        try {
            return Long.parseLong(lastPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format in URL: " + url);
        }
    }

    @Override
    public RealmIndexResponse fetchRealmIndex(Region region) {
        String path = BlizzardApiUrlBuilder.builder(region).realmIndex().build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build();
        return restApiClient.get(path, params, RealmIndexResponse.class);
    }

    @Override
    public Realm fetchRealm(Region region, String slug) {
        String path = BlizzardApiUrlBuilder.builder(region).realm(slug).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build();
        RealmResponse realmResponse = restApiClient.get(path, params, RealmResponse.class);
        return Realm.builder()
                .id(realmResponse.getId())
                .region(region)
                .name(realmResponse.getName())
                .connectedRealmId(extractIdFromUrl(realmResponse.getConnectedRealmHref()))
                .build();
    }

    @Override
    public void saveAll(Iterable<Realm> realms) {
        realmRepository.saveAll(realms);
    }

    @Override
    public long count() {
        return realmRepository.count();
    }
}
