package com.worbes.auctionhousetracker.repository;

import com.worbes.auctionhousetracker.entity.ItemClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemClassRepository extends JpaRepository<ItemClass, Long> {
    boolean existsByItemClassIdAndLocale(Long itemClassId, String locale);
}
