package com.worbes.adapter.jpa.repository.auction;

import com.worbes.adapter.jpa.entity.AuctionEntity;
import com.worbes.application.realm.model.RegionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface AuctionJpaRepository extends JpaRepository<AuctionEntity, Long> {
    List<AuctionEntity> findAllByRegionAndRealmIdAndAuctionIdIn(RegionType region, Long realmId, Set<Long> ids);
}
