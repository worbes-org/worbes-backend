package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import com.worbes.auctionhousetracker.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.DYNAMIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final RestApiClient restApiClient;
    private final AuctionRepository repository;

    @Override
    @Transactional
    public void updateAuctions(List<Auction> newAuctions, Region region) {
        updateAuctions(newAuctions, region, null);
    }

    @Override
    @Transactional
    public void updateAuctions(List<Auction> newAuctions, Region region, Long realmId) {
        validateInputs(newAuctions, region);

        log.info("🔄 경매 데이터 업데이트 시작 (Region: {}, RealmId: {})", region, realmId);

        List<Auction> activeAuctions = findActiveAuctions(region, realmId);
        Set<Long> existingAuctionIds = extractAuctionIds(activeAuctions);
        Set<Long> newAuctionIds = extractAuctionIds(newAuctions);

        List<Auction> endedAuctions = markEndedAuctions(activeAuctions, newAuctionIds);
        List<Auction> auctionsToSave = filterNewAuctions(newAuctions, existingAuctionIds);

        repository.saveAll(auctionsToSave);

        log.info("✅ 경매 데이터 업데이트 완료: 새로 추가된 경매 {}개, 종료된 경매 {}개",
                auctionsToSave.size(), endedAuctions.size());
    }

    // 입력 값 검증 메서드
    private void validateInputs(List<Auction> newAuctions, Region region) {
        if (region == null) {
            throw new IllegalArgumentException("Region must not be null");
        }
        if (newAuctions == null) {
            throw new IllegalArgumentException("New auctions list must not be null");
        }
    }

    // 활성 경매 조회
    private List<Auction> findActiveAuctions(Region region, Long realmId) {
        if (realmId == null) {
            return repository.findByActiveTrueAndRegion(region);
        } else {
            return repository.findByActiveTrueAndRegionAndRealmId(region, realmId);
        }
    }

    // Auction ID 추출
    private Set<Long> extractAuctionIds(List<Auction> auctions) {
        return auctions.stream()
                .map(Auction::getAuctionId)
                .collect(Collectors.toSet());
    }

    // 종료된 경매 처리: 새로운 경매 목록에 없는 기존 경매 상태 변경
    private List<Auction> markEndedAuctions(List<Auction> activeAuctions, Set<Long> newAuctionIds) {
        return activeAuctions.stream()
                .filter(auction -> !newAuctionIds.contains(auction.getAuctionId()))
                .peek(Auction::end)
                .toList();
    }

    // 신규 경매 필터링: 기존 경매 목록에 없는 것만 추출
    private List<Auction> filterNewAuctions(List<Auction> newAuctions, Set<Long> existingAuctionIds) {
        return newAuctions.stream()
                .filter(auction -> !existingAuctionIds.contains(auction.getAuctionId()))
                .toList();
    }

    @Override
    public AuctionResponse fetchCommodities(Region region) {
        return restApiClient.get(
                BlizzardApiUrlBuilder.builder(region).commodities().build(),
                BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build(),
                AuctionResponse.class
        );
    }

    @Override
    public AuctionResponse fetchAuctions(Region region, Long realmId) {
        return restApiClient.get(
                BlizzardApiUrlBuilder.builder(region).auctions(realmId).build(),
                BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build(),
                AuctionResponse.class);
    }

    @Override
    public void saveAuctions(List<Auction> auctions) {
        repository.saveAll(auctions);
    }
}
