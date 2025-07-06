package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.CloseAuctionUseCase;
import com.worbes.application.auction.port.in.SyncAuctionUseCase;
import com.worbes.application.auction.port.out.AuctionWriteRepository;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuctionWriteService implements SyncAuctionUseCase, CloseAuctionUseCase {

    private final AuctionWriteRepository auctionWriteRepository;

    @Override
    public Integer syncAll(List<Auction> auctions) {
        if (auctions.isEmpty()) return 0;

        return auctionWriteRepository.upsertAll(auctions);
    }

    @Override
    public Long closeAll(RegionType region, Long realmId, Set<Long> auctionIds) {
        if (region == null || auctionIds == null) {
            throw new IllegalArgumentException("auctionIds, region은 필수입니다.");
        }
        if (auctionIds.isEmpty()) {
            return 0L;
        }

        return auctionWriteRepository.updateEndedAtBy(region, realmId, auctionIds);
    }
}
