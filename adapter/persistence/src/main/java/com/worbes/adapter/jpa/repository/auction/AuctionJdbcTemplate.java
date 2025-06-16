package com.worbes.adapter.jpa.repository.auction;

import com.worbes.adapter.jpa.entity.AuctionEntity;

import java.util.List;

public interface AuctionJdbcTemplate {
    int saveAllIgnoreConflict(List<AuctionEntity> auctions);
}
