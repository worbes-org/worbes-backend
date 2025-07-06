package com.worbes.application.auction.service;

import com.worbes.application.auction.port.in.EndAuctionUseCase;
import com.worbes.application.auction.port.out.UpdateAuctionRepository;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdateAuctionService implements EndAuctionUseCase {

    private final UpdateAuctionRepository updateAuctionRepository;

    @Override
    public Long end(RegionType region, Long realmId, Set<Long> auctionIds) {
        if (region == null) {
            throw new IllegalArgumentException("region은 필수입니다.");
        }
        if (auctionIds == null || auctionIds.isEmpty()) {
            return 0L;
        }

        return updateAuctionRepository.updateEndedAt(region, realmId, auctionIds);
    }
}
