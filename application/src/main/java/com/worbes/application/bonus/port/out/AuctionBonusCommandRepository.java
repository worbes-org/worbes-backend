package com.worbes.application.bonus.port.out;

import com.worbes.application.bonus.port.model.AuctionBonus;

import java.util.List;

public interface AuctionBonusCommandRepository {
    void saveAllIgnoreConflict(List<AuctionBonus> auctionBonuses);
}
