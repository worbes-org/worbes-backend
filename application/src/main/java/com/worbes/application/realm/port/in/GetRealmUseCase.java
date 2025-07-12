package com.worbes.application.realm.port.in;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;

import java.util.List;

public interface GetRealmUseCase {
    List<Realm> getAll(RegionType region);
}
