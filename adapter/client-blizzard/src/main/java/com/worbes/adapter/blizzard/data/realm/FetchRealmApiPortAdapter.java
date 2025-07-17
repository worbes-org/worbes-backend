package com.worbes.adapter.blizzard.data.realm;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.adapter.blizzard.data.shared.BlizzardResponseValidator;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.FetchRealmApiPort;
import com.worbes.application.realm.port.out.FetchRealmApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FetchRealmApiPortAdapter implements FetchRealmApiPort {

    private final BlizzardApiClient apiClient;
    private final BlizzardApiUriFactory uriFactory;
    private final RealmResponseMapper realmResponseMapper;
    private final BlizzardResponseValidator validator;

    @Override
    public Set<String> fetchRealmIndex(RegionType region) {
        URI uri = uriFactory.realmIndexUri(region);

        return apiClient.fetch(uri, RealmIndexResponse.class)
                .realms().stream()
                .map(validator::validate)
                .map(RealmResponse::slug)
                .collect(Collectors.toSet());
    }

    @Override
    public CompletableFuture<List<FetchRealmApiResult>> fetchAllRealmsAsync(RegionType region, Set<String> slugs) {
        List<CompletableFuture<RealmResponse>> futures = slugs.stream()
                .map(slug -> fetchRealmAsync(region, slug))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .map(validator::validate)
                        .map(response -> realmResponseMapper.toDomain(response, region))
                        .toList()
                );
    }

    private CompletableFuture<RealmResponse> fetchRealmAsync(RegionType region, String slug) {
        URI uri = uriFactory.realmUri(region, slug);

        return apiClient.fetchAsync(uri, RealmResponse.class);
    }
}
