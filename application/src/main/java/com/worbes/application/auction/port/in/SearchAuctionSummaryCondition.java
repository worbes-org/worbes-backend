package com.worbes.application.auction.port.in;

import com.worbes.application.common.model.PageInfo;
import com.worbes.application.realm.model.RegionType;

import java.util.Set;

public record SearchAuctionSummaryCondition(
        RegionType region,
        Long realmId,
        Set<Long> itemIds,
        PageInfo pageInfo
) {
}
