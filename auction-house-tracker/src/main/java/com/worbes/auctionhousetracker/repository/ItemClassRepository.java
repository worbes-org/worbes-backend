package com.worbes.auctionhousetracker.repository;

import com.worbes.auctionhousetracker.entity.ItemClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ItemClassRepository extends JpaRepository<ItemClass, Long> {

    @Query("SELECT ic.id FROM ItemClass ic WHERE ic.id IN :itemClassIds")
    Set<Long> findExistingItemClassIds(@Param("itemClassIds") Set<Long> itemClassIds);

    @Query("SELECT ic FROM ItemClass ic WHERE ic.id IN :itemClassIds")
    List<ItemClass> findItemClassesByIds(@Param("itemClassIds") Set<Long> itemClassIds);
}
