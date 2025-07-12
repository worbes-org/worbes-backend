package com.worbes.application.auction.port.out;

import com.worbes.application.common.model.PageInfo;
import com.worbes.application.item.model.Item;
import com.worbes.application.realm.model.RegionType;

import java.util.List;

public record AuctionSummarySearchCondition(
        RegionType region,
        Long realmId,
        List<Item> items,
        PageInfo pageInfo
) {
}
