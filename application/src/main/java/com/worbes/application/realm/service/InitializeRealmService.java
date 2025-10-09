package com.worbes.application.realm.service;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.InitializeRealmUseCase;
import com.worbes.application.realm.port.out.FetchRealmApiPort;
import com.worbes.application.realm.port.out.FindRealmPort;
import com.worbes.application.realm.port.out.SaveRealmPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile("batch")
@RequiredArgsConstructor
public class InitializeRealmService implements InitializeRealmUseCase {

    private final SaveRealmPort saveRealmPort;
    private final FindRealmPort findRealmPort;
    private final FetchRealmApiPort fetchRealmApiPort;

    @Override
    public List<Realm> execute(RegionType region) {
        Set<String> missingSlugs = findMissingRealmSlugs(region);

        log.info("[Realm 초기화] 누락된 Realm 슬러그 목록: {} (총 {}개)", missingSlugs, missingSlugs.size());
        if (missingSlugs.isEmpty()) return List.of();

        List<CompletableFuture<Realm>> futures = missingSlugs.stream()
                .map(slug -> fetchRealmApiPort.fetchAsync(region, slug))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        Set<Realm> realms = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Realm> saved = saveRealmPort.saveAll(realms);
        log.info("[Realm 초기화] 저장 완료: 총 {}개", saved.size());
        return saved;
    }

    private Set<String> findMissingRealmSlugs(RegionType region) {
        Set<String> fetched = fetchRealmApiPort.fetchRealmIndex(region);
        Set<String> result = new HashSet<>(fetched);
        Set<String> existingSlugs = findRealmPort.findSlugByRegion(region);
        result.removeAll(existingSlugs);

        return result;
    }
}
