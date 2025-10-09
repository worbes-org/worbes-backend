package com.worbes.application.auction.service;

import com.worbes.application.auction.port.in.DeleteAuctionUseCase;
import com.worbes.application.auction.port.out.DeleteAuctionPort;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("batch")
@RequiredArgsConstructor
public class DeleteAuctionService implements DeleteAuctionUseCase {

    private final DeleteAuctionPort deleteAuctionPort;

    @Override
    public long execute(RegionType region, Long realmId) {
        if (region == null) {
            throw new IllegalArgumentException("region은 필수입니다.");
        }

        return deleteAuctionPort.deleteAll(region, realmId);
    }
}
