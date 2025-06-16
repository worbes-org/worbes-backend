package com.worbes.application.realm.service;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.InitializeRealmUseCase;
import com.worbes.application.realm.port.out.CreateRealmRepository;
import com.worbes.application.realm.port.out.FindRealmRepository;
import com.worbes.application.realm.port.out.RealmFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitializeRealmService implements InitializeRealmUseCase {

    private final CreateRealmRepository createRealmRepository;
    private final FindRealmRepository findRealmRepository;
    private final RealmFactory realmFactory;
    private final RealmFetcher realmFetcher;

    @Override
    public List<Realm> initialize(RegionType region) {
        Set<String> missingSlugs = findMissingRealmSlugs(region);
        log.info("[Realm 초기화] 누락된 Realm 슬러그 목록: {} (총 {}개)", missingSlugs, missingSlugs.size());
        List<Realm> realms = realmFetcher.fetchAllRealmsAsync(region, missingSlugs)
                .thenApply(fetchResults -> fetchResults.stream()
                        .map(realmFactory::create)
                        .toList()
                )
                .join();


        List<Realm> saved = createRealmRepository.saveAll(realms);
        log.info("[Realm 초기화] 저장 완료: 총 {}개", saved.size());
        return saved;
    }

    private Set<String> findMissingRealmSlugs(RegionType region) {
        Set<String> fetched = realmFetcher.fetchRealmIndex(region);
        Set<String> result = new HashSet<>(fetched);
        Set<String> existingSlugs = findRealmRepository.findSlugByRegion(region);
        result.removeAll(existingSlugs);

        return result;
    }
}
