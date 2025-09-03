package com.worbes.application.auction.port.in;

import com.worbes.application.common.model.PageInfo;
import com.worbes.application.item.model.Item;
import com.worbes.application.realm.model.RegionType;

import java.util.List;

public record SearchAuctionSummaryQuery(
        RegionType region,
        Long realmId,
        List<Item> items,
        Integer minItemLevel,
        Integer maxItemLevel,
        PageInfo pageInfo
) {
}
