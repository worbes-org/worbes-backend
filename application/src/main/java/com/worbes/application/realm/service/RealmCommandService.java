package com.worbes.application.realm.service;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.InitializeRealmUseCase;
import com.worbes.application.realm.port.out.RealmApiFetcher;
import com.worbes.application.realm.port.out.RealmQueryRepository;
import com.worbes.application.realm.port.out.RealmCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealmCommandService implements InitializeRealmUseCase {

    private final RealmCommandRepository realmCommandRepository;
    private final RealmQueryRepository realmQueryRepository;
    private final RealmApiFetcher realmApiFetcher;

    @Override
    public List<Realm> initialize(RegionType region) {
        Set<String> missingSlugs = findMissingRealmSlugs(region);

        log.info("[Realm 초기화] 누락된 Realm 슬러그 목록: {} (총 {}개)", missingSlugs, missingSlugs.size());
        if (missingSlugs.isEmpty()) return List.of();

        Set<Realm> realms = realmApiFetcher.fetchAllRealmsAsync(region, missingSlugs)
                .thenApply(HashSet::new)
                .join();


        List<Realm> saved = realmCommandRepository.saveAll(realms);
        log.info("[Realm 초기화] 저장 완료: 총 {}개", saved.size());
        return saved;
    }

    private Set<String> findMissingRealmSlugs(RegionType region) {
        Set<String> fetched = realmApiFetcher.fetchRealmIndex(region);
        Set<String> result = new HashSet<>(fetched);
        Set<String> existingSlugs = realmQueryRepository.findSlugByRegion(region);
        result.removeAll(existingSlugs);

        return result;
    }
}
