package com.worbes.application.auction.port.in;

import com.worbes.application.auction.model.AuctionDetail;
import com.worbes.application.realm.model.RegionType;

public interface GetAuctionDetailUseCase {
    AuctionDetail getAuctionDetail(Long itemId, RegionType region, Long realmId);
}
