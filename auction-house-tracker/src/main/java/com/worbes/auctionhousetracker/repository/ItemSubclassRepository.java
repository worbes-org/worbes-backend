package com.worbes.auctionhousetracker.repository;

import com.worbes.auctionhousetracker.entity.ItemSubclass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ItemSubclassRepository extends JpaRepository<ItemSubclass, Long> {

    @Query("SELECT s FROM ItemSubclass s JOIN FETCH s.itemClass WHERE s.itemClass.id IN :classIds")
    List<ItemSubclass> findByItemClassIdIn(@Param("classIds") Set<Long> classIds);
}
