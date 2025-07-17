package com.worbes.adapter.mybatis.auction;

import com.worbes.application.auction.model.Auction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuctionMapper {
    int upsertAll(@Param("auctions") List<Auction> auctions);
}
