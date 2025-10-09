package com.worbes.application.realm.port.in;

import com.worbes.application.realm.model.RegionType;

import java.util.List;

public interface GetConnectedRealmIdUseCase {
    List<Long> execute(RegionType region);
}
