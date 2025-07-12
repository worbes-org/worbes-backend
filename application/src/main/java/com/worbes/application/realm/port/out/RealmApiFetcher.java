package com.worbes.application.realm.port.out;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface RealmApiFetcher {
    Set<String> fetchRealmIndex(RegionType region);

    CompletableFuture<List<Realm>> fetchAllRealmsAsync(RegionType region, Set<String> slugs);
}
