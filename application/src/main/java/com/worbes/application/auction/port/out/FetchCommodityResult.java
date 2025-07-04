package com.worbes.application.auction.port.out;

import com.worbes.application.realm.model.RegionType;

public record FetchCommodityResult(
        Long id,
        Long itemId,
        Long quantity,
        Long unitPrice,
        RegionType region
) {
}
