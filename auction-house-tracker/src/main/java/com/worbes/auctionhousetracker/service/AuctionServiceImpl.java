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
    public void updateAuctions(List<Auction> newAuctions, Region region, Long realmId) {
        if (region == null) {
            throw new IllegalArgumentException("Region must not be null");
        }
        if (newAuctions == null) {
            throw new IllegalArgumentException("New auctions list must not be null");
        }
        log.info("🔄 경매 데이터 업데이트 시작 (Region: {}, RealmId: {})", region, realmId);

        // 1. 현재 활성화된 경매 목록 조회
        List<Auction> activeAuctions;
        if (realmId == null) {
            activeAuctions = repository.findByActiveTrueAndRegion(region);
        } else {
            activeAuctions = repository.findByActiveTrueAndRegionAndRealmId(region, realmId);
        }

        // 2. 기존 경매 ID 목록 생성
        Set<Long> existingAuctionIds = activeAuctions.stream()
                .map(Auction::getAuctionId)
                .collect(Collectors.toSet());

        // 3. 새로운 경매 ID 목록 생성
        Set<Long> newAuctionIds = newAuctions.stream()
                .map(Auction::getAuctionId)
                .collect(Collectors.toSet());

        // 4. 종료된 경매 처리 (새로운 목록에 없는 기존 경매)
        List<Auction> endedAuctions = activeAuctions.stream()
                .filter(auction -> !newAuctionIds.contains(auction.getAuctionId()))
                .peek(Auction::end) // 종료 처리
                .toList();

        // 5. 새로운 경매만 저장 (기존에 없던 것들만)
        List<Auction> auctionsToSave = newAuctions.stream()
                .filter(auction -> !existingAuctionIds.contains(auction.getAuctionId()))
                .toList();

        repository.saveAll(auctionsToSave);
        log.info("✅ 경매 데이터 업데이트 완료: 새로 추가된 경매 {}개, 종료된 경매 {}개",
                auctionsToSave.size(),
                endedAuctions.size()
        );
    }

    @Override
    @Transactional
    public void updateAuctions(List<Auction> newAuctions, Region region) {
        updateAuctions(newAuctions, region, null);
    }

    @Override
    public List<Auction> fetchCommodities(Region region) {
        return restApiClient.get(
                        BlizzardApiUrlBuilder.builder(region).commodities().build(),
                        BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build(),
                        AuctionResponse.class
                )
                .getAuctions()
                .stream()
                .map(dto -> new Auction(dto, region))
                .toList();
    }

    @Override
    public List<Auction> fetchAuctions(Region region, Long realmId) {
        return restApiClient.get(
                        BlizzardApiUrlBuilder.builder(region).auctions(realmId).build(),
                        BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build(),
                        AuctionResponse.class
                )
                .getAuctions()
                .stream()
                .map(dto -> new Auction(dto, region, realmId))
                .toList();
    }

    @Override
    public void saveAuctions(List<Auction> auctions) {
        repository.saveAll(auctions);
    }
}
