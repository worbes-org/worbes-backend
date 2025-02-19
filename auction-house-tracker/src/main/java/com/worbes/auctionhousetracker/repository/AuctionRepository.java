package com.worbes.auctionhousetracker.repository;


import com.worbes.auctionhousetracker.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
