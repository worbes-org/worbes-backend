package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.AuctionDto;
import com.worbes.auctionhousetracker.dto.mapper.AuctionUpdateCommand;
import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.Item;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import com.worbes.auctionhousetracker.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository repository;
    private final ItemService itemService;
    private final RealmService realmService;


    @Override
    @Transactional
    public void updateAuctions(AuctionUpdateCommand command) {
        Realm realm = realmService.get(command.getRegion(), command.getRealmId());
        RegionType region = command.getRegion();
        List<AuctionDto> dtos = command.getAuctions();
        log.info("🔄 경매 데이터 업데이트 시작 (Region: {}, Realm: {}, 수신 개수: {})", region, realm, dtos.size());

        Set<Auction> newAuctions = convertDtoToEntity(dtos, region, realm);
        Set<Auction> activeAuctions = findActiveAuctions(newAuctions, region, realm);

        deactivateClosedAuction(newAuctions, activeAuctions);
        saveNewAuctions(newAuctions, activeAuctions);
    }

    private Set<Auction> convertDtoToEntity(List<AuctionDto> dtos, RegionType region, Realm realm) {
        Map<Long, Item> requiredItems = getRequiredItems(dtos);

        return dtos.stream()
                .map(dto -> {
                            Item item = requiredItems.get(dto.getItemId());
                            return Auction.from(dto, item, realm, region);
                        }
                )
                .filter(auction -> auction.getItem() != null)
                .collect(toSet());
    }

    private Map<Long, Item> getRequiredItems(List<AuctionDto> dtos) {
        Set<Long> itemIds = dtos.stream()
                .map(AuctionDto::getItemId)
                .collect(toSet());

        Map<Long, Item> result = itemService.getItemsBy(itemIds);

        long nullItemCount = dtos.stream().filter(dto -> !result.containsKey(dto.getItemId())).count();
        if (nullItemCount > 0) log.warn("⚠️ 매핑 실패한 itemId 수: {} / 전체 수신: {}", nullItemCount, dtos.size());

        return result;
    }

    private void deactivateClosedAuction(Set<Auction> newAuctions, Set<Auction> activeAuctions) {
        Set<Auction> endedAuctions = new HashSet<>(activeAuctions);
        endedAuctions.removeAll(newAuctions);
        endedAuctions.forEach(Auction::end);
        log.info("종료된 경매 {}개", endedAuctions.size());
    }

    private void saveNewAuctions(Set<Auction> newAuctions, Set<Auction> activeAuctions) {
        Set<Auction> auctionsToInsert = new HashSet<>(newAuctions);
        auctionsToInsert.removeAll(activeAuctions);
        repository.saveAll(auctionsToInsert);
        log.info("💾 신규 경매 저장 완료: {}건", auctionsToInsert.size());
    }

    private Set<Auction> findActiveAuctions(Set<Auction> newAuctions, RegionType region, Realm realm) {
        Set<Long> newIds = newAuctions.stream().map(Auction::getAuctionId).collect(toSet());
        Set<Auction> result = repository.findByAuctionIdInAndActiveTrueAndRegionAndRealm(newIds, region, realm);
        log.info("🧹 현재 DB에 존재하는 활성 경매 수: {}", result.size());
        return result;
    }
}
