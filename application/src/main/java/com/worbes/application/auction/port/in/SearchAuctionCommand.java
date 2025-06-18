package com.worbes.application.auction.port.in;

import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.realm.model.RegionType;

public record SearchAuctionCommand(
        RegionType region,
        Long realmId,
        LocaleCode locale
) {
}
