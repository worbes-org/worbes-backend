package com.worbes.application.realm.port.in;

import com.worbes.application.realm.model.RegionType;

import java.util.List;

public interface FindConnectedRealmUseCase {
    List<Long> findConnectedRealmId(RegionType region);
}
