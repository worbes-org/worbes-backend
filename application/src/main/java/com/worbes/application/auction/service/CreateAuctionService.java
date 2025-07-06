package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.CreateAuctionUseCase;
import com.worbes.application.auction.port.out.CreateAuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateAuctionService implements CreateAuctionUseCase {

    private final CreateAuctionRepository createAuctionRepository;

    @Override
    public int createAuctions(List<Auction> auctions) {
        return createAuctionRepository.upsertAllQuantityIfChanged(auctions);
    }
}
