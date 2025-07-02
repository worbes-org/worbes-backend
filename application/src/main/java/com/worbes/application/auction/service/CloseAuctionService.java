package com.worbes.application.auction.service;

import com.worbes.application.auction.port.in.CloseAuctionCommand;
import com.worbes.application.auction.port.in.CloseAuctionUseCase;
import com.worbes.application.auction.port.out.UpdateAuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CloseAuctionService implements CloseAuctionUseCase {

    private final UpdateAuctionRepository updateAuctionRepository;

    @Override
    public Long closeAuctions(CloseAuctionCommand command) {
        return updateAuctionRepository.deactivate(
                command.region(),
                command.realmId(),
                command.auctionIds()
        );
    }
}
