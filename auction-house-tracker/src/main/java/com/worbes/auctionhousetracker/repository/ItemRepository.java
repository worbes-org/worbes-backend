package com.worbes.auctionhousetracker.repository;

import com.worbes.auctionhousetracker.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemCustomRepository {
    @Query("SELECT i.id FROM Item i WHERE i.id IN :ids")
    Set<Long> findItemIdByItemIdIn(@Param("ids") Set<Long> ids);

    List<Item> findAllByIdIn(Set<Long> itemIds);
}
