package com.worbes.adapter.mybatis.auction;

import com.worbes.application.auction.model.AuctionItemBonus;
import com.worbes.application.auction.port.out.SaveAuctionItemBonusPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuctionItemBonusMybatisRepository implements SaveAuctionItemBonusPort {

    private final AuctionItemBonusMapper mapper;

    @Override
    public void saveAllIgnoreConflict(List<AuctionItemBonus> auctionItemBonuses) {
        mapper.insertAuctionItemBonuses(auctionItemBonuses);
    }
}
