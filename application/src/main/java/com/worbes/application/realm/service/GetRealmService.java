package com.worbes.application.realm.service;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.GetRealmUseCase;
import com.worbes.application.realm.port.out.FindRealmPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRealmService implements GetRealmUseCase {

    private final FindRealmPort findRealmPort;

    @Override
    public List<Realm> execute(RegionType region) {
        return findRealmPort.findByRegion(region);
    }
}
