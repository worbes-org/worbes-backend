package com.worbes.adapter.persistence.mybatis.auction;

import com.worbes.application.realm.model.RegionType;

import java.util.List;

public record AuctionMybatisDto(
        Long id,
        Long itemId,
        Integer quantity,
        Long price,
        RegionType region,
        Long realmId,
        List<Long> itemBonus
) {
}
