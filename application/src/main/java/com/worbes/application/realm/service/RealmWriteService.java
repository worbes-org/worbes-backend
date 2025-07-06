package com.worbes.application.realm.service;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.InitializeRealmUseCase;
import com.worbes.application.realm.port.out.RealmFetcher;
import com.worbes.application.realm.port.out.RealmReadRepository;
import com.worbes.application.realm.port.out.RealmWriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealmWriteService implements InitializeRealmUseCase {

    private final RealmWriteRepository realmWriteRepository;
    private final RealmReadRepository realmReadRepository;
    private final RealmFactory realmFactory;
    private final RealmFetcher realmFetcher;

    @Override
    public List<Realm> initialize(RegionType region) {
        Set<String> missingSlugs = findMissingRealmSlugs(region);

        log.info("[Realm 초기화] 누락된 Realm 슬러그 목록: {} (총 {}개)", missingSlugs, missingSlugs.size());
        if (missingSlugs.isEmpty()) return List.of();

        Set<Realm> realms = realmFetcher.fetchAllRealmsAsync(region, missingSlugs)
                .thenApply(fetchResults -> fetchResults.stream()
                        .map(realmFactory::create)
                        .collect(Collectors.toSet())
                )
                .join();


        List<Realm> saved = realmWriteRepository.saveAll(realms);
        log.info("[Realm 초기화] 저장 완료: 총 {}개", saved.size());
        return saved;
    }

    private Set<String> findMissingRealmSlugs(RegionType region) {
        Set<String> fetched = realmFetcher.fetchRealmIndex(region);
        Set<String> result = new HashSet<>(fetched);
        Set<String> existingSlugs = realmReadRepository.findSlugByRegion(region);
        result.removeAll(existingSlugs);

        return result;
    }
}
