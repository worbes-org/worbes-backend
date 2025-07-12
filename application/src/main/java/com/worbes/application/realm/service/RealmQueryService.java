package com.worbes.application.realm.service;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.GetConnectedRealmIdUseCase;
import com.worbes.application.realm.port.in.GetRealmUseCase;
import com.worbes.application.realm.port.out.RealmQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RealmQueryService implements GetConnectedRealmIdUseCase, GetRealmUseCase {

    private final RealmQueryRepository realmQueryRepository;

    @Override
    public List<Long> getAllConnectedRealmId(RegionType region) {
        return realmQueryRepository.findDistinctConnectedRealmIdByRegion(region);
    }

    @Override
    public List<Realm> getAll(RegionType region) {
        return realmQueryRepository.findByRegion(region);
    }
}
