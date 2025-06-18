package com.worbes.adapter.jpa.repository.auction;

import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.realm.model.RegionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface AuctionQueryRepository {
    Page<Long> findAuctionIdsByRegionAndRealmIdAndActiveTrue(RegionType region, Long realmId, Pageable pageable);

    Long deactivateByRegionAndRealmAndAuctionIdsIn(RegionType region, Long realmId, Set<Long> ids);

    List<SearchAuctionSummaryResult> findAuctionSummariesBy(RegionType region, Long realmId, Set<Long> itemIds);
}
