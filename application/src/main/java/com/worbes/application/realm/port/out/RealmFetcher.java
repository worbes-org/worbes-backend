package com.worbes.application.realm.port.out;

import com.worbes.application.realm.model.RegionType;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface RealmFetcher {
    Set<String> fetchRealmIndex(RegionType region);

    CompletableFuture<List<RealmFetchResult>> fetchAllRealmsAsync(RegionType region, Set<String> slugs);
}
