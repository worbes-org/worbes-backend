package com.worbes.application.realm.service;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.GetConnectedRealmIdUseCase;
import com.worbes.application.realm.port.in.GetRealmUseCase;
import com.worbes.application.realm.port.out.RealmReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RealmReadService implements GetConnectedRealmIdUseCase, GetRealmUseCase {

    private final RealmReadRepository realmReadRepository;

    @Override
    public List<Long> getConnectedRealmId(RegionType region) {
        return realmReadRepository.findDistinctConnectedRealmIdByRegion(region);
    }

    @Override
    public List<Realm> get(RegionType region) {
        return realmReadRepository.findByRegion(region);
    }
}
