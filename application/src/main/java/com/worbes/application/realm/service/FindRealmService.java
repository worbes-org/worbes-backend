package com.worbes.application.realm.service;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.FindConnectedRealmUseCase;
import com.worbes.application.realm.port.in.FindRealmByRegionUseCase;
import com.worbes.application.realm.port.out.FindRealmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindRealmService implements FindConnectedRealmUseCase, FindRealmByRegionUseCase {

    private final FindRealmRepository findRealmRepository;

    @Override
    public List<Long> findConnectedRealmId(RegionType region) {
        return findRealmRepository.findDistinctConnectedRealmIdByRegion(region);
    }

    @Override
    public List<Realm> findByRegion(RegionType region) {
        return findRealmRepository.findByRegion(region);
    }
}
