package com.worbes.auctionhousetracker.repository;

import com.worbes.auctionhousetracker.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
