package com.worbes.application.realm.port.out;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;

import java.util.List;
import java.util.Set;

public interface RealmQueryRepository {
    List<Long> findDistinctConnectedRealmIdByRegion(RegionType region);

    Set<String> findSlugByRegion(RegionType region);

    List<Realm> findByRegion(RegionType region);
}
