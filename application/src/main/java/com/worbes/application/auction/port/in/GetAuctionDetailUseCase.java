package com.worbes.application.auction.port.in;

import com.worbes.application.auction.model.AuctionDetail;
import com.worbes.application.realm.model.RegionType;

public interface GetAuctionDetailUseCase {
    AuctionDetail getDetail(Long itemId, RegionType region, Long realmId);
}
