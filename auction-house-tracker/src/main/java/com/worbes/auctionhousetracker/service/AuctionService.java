package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;

import java.util.List;

public interface AuctionService {

    /**
     * 특정 지역의 경매장 데이터를 업데이트합니다.
     * Commodities API를 호출하여 모든 realm의 공통 아이템 경매 데이터를 처리합니다.
     * <h3>처리 과정</h3>
     * <ul>
     *     <li>새로운 경매 목록에 없는 기존 경매는 비활성화(active=false)됩니다.</li>
     *     <li>기존에 없던 새로운 경매만 저장됩니다.</li>
     * </ul>
     *
     * @param newAuctions 새로운 경매 데이터 목록
     * @param region      대상 지역
     * @throws IllegalArgumentException newAuctions 또는 region이 null인 경우
     * @see #updateAuctions(List, Region, Long)
     */
    void updateAuctions(List<Auction> newAuctions, Region region);

    /**
     * 특정 지역과 realm의 경매장 데이터를 업데이트합니다.
     * <h3>API 호출 구분</h3>
     * <ul>
     *     <li>realmId가 null인 경우: Commodities API를 호출하여 모든 realm의 공통 아이템 경매 데이터를 처리</li>
     *     <li>realmId가 존재하는 경우: Auctions API를 호출하여 특정 realm의 경매 데이터를 처리</li>
     * </ul>
     * <h3>처리 과정</h3>
     * <ul>
     *     <li>새로운 경매 목록에 없는 기존 경매는 비활성화(active=false)됩니다.</li>
     *     <li>기존에 없던 새로운 경매만 저장됩니다.</li>
     * </ul>
     *
     * @param newAuctions 새로운 경매 데이터 목록
     * @param region      대상 지역
     * @param realmId     realm ID (null인 경우 commodities 경매장 데이터 처리)
     * @throws IllegalArgumentException newAuctions 또는 region이 null인 경우
     */
    void updateAuctions(List<Auction> newAuctions, Region region, Long realmId);

    /**
     * 특정 지역의 경매장 데이터를 Blizzard API에서 가져옵니다.
     * Commodities API를 호출하여 모든 realm의 공통 아이템 경매 데이터를 조회합니다.
     * (예: 재료, 소모품, 보석 등)
     *
     * @param region 대상 지역
     * @return 경매 데이터 목록
     */
    List<Auction> fetchAuctions(Region region);

    /**
     * 특정 지역과 realm의 경매장 데이터를 Blizzard API에서 가져옵니다.
     * realmId가 null인 경우: Commodities API 호출 (/data/wow/auctions/commodities)
     * realmId가 존재하는 경우: Auctions API 호출 (/data/wow/connected-realm/{realmId}/auctions)
     *
     * @param region  대상 지역
     * @param realmId realm ID (null인 경우 commodities 경매장 데이터 조회)
     * @return 경매 데이터 목록
     */
    List<Auction> fetchAuctions(Region region, Integer realmId);

    void saveAuctions(List<Auction> mockAuctions);
}
