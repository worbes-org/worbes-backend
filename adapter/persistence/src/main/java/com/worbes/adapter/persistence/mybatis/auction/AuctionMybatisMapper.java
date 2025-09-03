package com.worbes.adapter.persistence.mybatis.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.GetAuctionDetailQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuctionMybatisMapper {
    int saveAll(@Param("auctions") List<Auction> auctions);

    List<AuctionMybatisDto> findBy(@Param("query") GetAuctionDetailQuery query);
}
