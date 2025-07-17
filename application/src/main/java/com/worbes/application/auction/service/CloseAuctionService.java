package com.worbes.application.auction.service;

import com.worbes.application.auction.port.in.CloseAuctionUseCase;
import com.worbes.application.auction.port.out.UpdateAuctionPort;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Profile("batch")
@RequiredArgsConstructor
public class CloseAuctionService implements CloseAuctionUseCase {

    private final UpdateAuctionPort updateAuctionPort;

    @Override
    public long execute(RegionType region, Long realmId, Set<Long> auctionIds) {
        if (region == null || auctionIds == null) {
            throw new IllegalArgumentException("auctionIds, region은 필수입니다.");
        }
        if (auctionIds.isEmpty()) {
            return 0L;
        }

        return updateAuctionPort.updateEndedAt(region, realmId, auctionIds);
    }
}
