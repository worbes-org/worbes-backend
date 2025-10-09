package com.worbes.application.realm.service;

import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.GetConnectedRealmIdUseCase;
import com.worbes.application.realm.port.out.FindRealmPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("batch")
@RequiredArgsConstructor
public class GetConnectedRealmIdUseService implements GetConnectedRealmIdUseCase {

    private final FindRealmPort findRealmPort;

    @Override
    public List<Long> execute(RegionType region) {
        return findRealmPort.findDistinctConnectedRealmIdByRegion(region);
    }
}
