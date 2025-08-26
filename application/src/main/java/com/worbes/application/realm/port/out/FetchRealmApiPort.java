package com.worbes.application.realm.port.out;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface FetchRealmApiPort {
    Set<String> fetchRealmIndex(RegionType region);

    CompletableFuture<Realm> fetchAsync(RegionType region, String slug);
}
