package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.CloseAuctionUseCase;
import com.worbes.application.auction.port.in.SyncAuctionUseCase;
import com.worbes.application.auction.port.out.AuctionCommandRepository;
import com.worbes.application.bonus.port.model.AuctionBonus;
import com.worbes.application.bonus.port.out.AuctionBonusCommandRepository;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuctionCommandService implements SyncAuctionUseCase, CloseAuctionUseCase {

    private final AuctionCommandRepository auctionCommandRepository;
    private final AuctionBonusCommandRepository auctionBonusCommandRepository;

    @Override
    public Integer syncAll(List<Auction> auctions) {
        if (auctions.isEmpty()) return 0;
        saveAuctionBonus(auctions);

        return auctionCommandRepository.upsertAll(auctions);
    }

    private void saveAuctionBonus(List<Auction> auctions) {
        List<AuctionBonus> auctionBonuses = new ArrayList<>();
        for (Auction auction : auctions) {
            List<AuctionBonus> list = auction.getItemBonus().stream()
                    .map(bonusId -> new AuctionBonus(auction.getId(), bonusId))
                    .toList();
            auctionBonuses.addAll(list);
        }
        auctionBonusCommandRepository.saveAllIgnoreConflict(auctionBonuses);
    }

    @Override
    public Long closeAll(RegionType region, Long realmId, Set<Long> auctionIds) {
        if (region == null || auctionIds == null) {
            throw new IllegalArgumentException("auctionIds, region은 필수입니다.");
        }
        if (auctionIds.isEmpty()) {
            return 0L;
        }

        return auctionCommandRepository.updateEndedAtBy(region, realmId, auctionIds);
    }
}
