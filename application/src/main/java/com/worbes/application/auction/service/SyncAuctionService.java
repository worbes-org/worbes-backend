package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.model.AuctionItemBonus;
import com.worbes.application.auction.port.in.SyncAuctionUseCase;
import com.worbes.application.auction.port.out.SaveAuctionItemBonusPort;
import com.worbes.application.auction.port.out.UpsertAuctionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("batch")
@RequiredArgsConstructor
public class SyncAuctionService implements SyncAuctionUseCase {


    private final UpsertAuctionPort upsertAuctionPort;
    private final SaveAuctionItemBonusPort saveAuctionItemBonusPort;

    @Override
    public int execute(List<Auction> auctions) {
        if (auctions.isEmpty()) return 0;
        int result = upsertAuctionPort.upsertAll(auctions);
        saveAuctionBonus(auctions);

        return result;
    }

    private void saveAuctionBonus(List<Auction> auctions) {
        List<AuctionItemBonus> auctionItemBonuses = new ArrayList<>();
        for (Auction auction : auctions) {
            List<AuctionItemBonus> list = auction.getItemBonus().stream()
                    .map(bonusId -> new AuctionItemBonus(auction.getId(), bonusId))
                    .toList();
            auctionItemBonuses.addAll(list);
        }

        if (!auctionItemBonuses.isEmpty()) {
            saveAuctionItemBonusPort.saveAllIgnoreConflict(auctionItemBonuses);
        }
    }
}
