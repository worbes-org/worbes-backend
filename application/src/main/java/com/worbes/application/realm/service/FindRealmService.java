package com.worbes.application.realm.service;

import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.FindConnectedRealmUseCase;
import com.worbes.application.realm.port.out.FindRealmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindRealmService implements FindConnectedRealmUseCase {

    private final FindRealmRepository findRealmRepository;

    @Override
    public List<Long> findConnectedRealmId(RegionType region) {
        return findRealmRepository.findDistinctConnectedRealmIdByRegion(region);
    }
}
