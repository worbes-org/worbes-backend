package com.worbes.auctionhousetracker.repository;


import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Set<Auction> findByAuctionIdInAndActiveTrueAndRegionAndRealm(Set<Long> auctionIds, RegionType region, Realm realm);
}
