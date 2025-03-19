package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.entity.enums.Region;

import java.util.List;

public interface RealmService {

    /**
     * API에서 받은 Realm 목록과 DB에 저장된 Realm 목록을 비교하여,
     * 해당 Region에서 DB에 존재하지 않는 Realm slug 리스트를 반환한다.
     *
     * @param response Blizzard API에서 가져온 Realm 목록 응답 객체
     * @param region   필터링할 WoW 지역 (예: KR, US 등)
     * @return DB에 저장되지 않은 Realm slug 목록
     */
    List<String> getMissingRealmSlugs(RealmIndexResponse response, Region region);
}
