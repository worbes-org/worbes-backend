package com.worbes.application.realm.port.in;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;

import java.util.List;

public interface FindRealmByRegionUseCase {
    List<Realm> findByRegion(RegionType region);
}
