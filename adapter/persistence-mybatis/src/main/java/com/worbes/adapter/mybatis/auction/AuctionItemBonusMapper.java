package com.worbes.adapter.mybatis.auction;

import com.worbes.application.auction.model.AuctionItemBonus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuctionItemBonusMapper {
    void insertAuctionItemBonuses(@Param("bonuses") List<AuctionItemBonus> bonuses);
}
