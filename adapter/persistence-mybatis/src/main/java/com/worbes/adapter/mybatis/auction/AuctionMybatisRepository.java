package com.worbes.adapter.mybatis.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.out.UpsertAuctionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuctionMybatisRepository implements UpsertAuctionPort {

    private final AuctionMapper mapper;

    @Override
    public int upsertAll(List<Auction> auctions) {
        return mapper.upsertAll(auctions);
    }
}
