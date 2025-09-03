package com.worbes.adapter.persistence.mybatis.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.GetAuctionDetailQuery;
import com.worbes.application.auction.port.out.FindAuctionPort;
import com.worbes.application.auction.port.out.SaveAuctionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuctionMybatisAdapter implements SaveAuctionPort, FindAuctionPort {

    private final AuctionMybatisMapper mapper;

    @Override
    public int saveAll(List<Auction> auctions) {
        if (auctions.isEmpty()) return 0;
        return mapper.saveAll(auctions);
    }

    @Override
    public List<Auction> findBy(GetAuctionDetailQuery query) {
        return mapper.findBy(query)
                .stream()
                .map(dto -> new Auction(
                                dto.id(),
                                dto.itemId(),
                                dto.price(),
                                dto.price(),
                                dto.region(),
                                dto.itemBonus(),
                                dto.quantity()
                        )
                )
                .toList();
    }
}
