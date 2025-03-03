package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RealmService {

    /**
     * 서버 인덱스 정보를 조회합니다.
     */
    RealmIndexResponse fetchRealmIndex(Region region);

    /**
     * 특정 서버의 상세 정보를 조회합니다.
     */
    Realm fetchRealm(Region region, String slug);

    /**
     * 특정 서버의 상세 정보를 비동기로 조회합니다.
     */
    CompletableFuture<Realm> fetchRealmAsync(Region region, String slug);

    /**
     * 여러 서버 정보를 저장합니다.
     */
    void saveAll(Iterable<Realm> realms);

    /**
     * 저장된 서버의 총 개수를 반환합니다.
     */
    long count();

    /**
     * 서버 데이터가 이미 초기화되었는지 확인합니다.
     */
    boolean isRealmInitialized();

    /**
     * 특정 지역의 모든 서버 데이터를 초기화합니다.
     */
    CompletableFuture<Void> fetchAndSaveRealms(Region region);

    /**
     * 특정 Region에 해당하는 모든 connected realm의 ID 목록을 반환합니다.
     */
    List<Long> getConnectedRealmIdsByRegion(Region region);
}
