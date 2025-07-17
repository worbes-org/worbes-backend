package com.worbes.application.auction.port.out;

import com.worbes.application.auction.model.AuctionItemBonus;

import java.util.List;

public interface SaveAuctionItemBonusPort {
    void saveAllIgnoreConflict(List<AuctionItemBonus> auctionItemBonuses);
}
