package com.worbes.auctionhousetracker.repository;


import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByActiveTrueAndRegion(Region region);

    List<Auction> findByActiveTrueAndRegionAndRealmId(Region region, Long realmId);
}
