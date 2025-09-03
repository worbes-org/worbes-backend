package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.SaveAuctionUseCase;
import com.worbes.application.auction.port.out.SaveAuctionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("batch")
@RequiredArgsConstructor
public class SaveAuctionService implements SaveAuctionUseCase {

    private final SaveAuctionPort saveAuctionPort;

    @Override
    public long execute(List<Auction> auctions) {
        if (auctions.isEmpty()) return 0;

        return saveAuctionPort.saveAll(auctions);
    }
}
