package com.worbes.auctionhousetracker.repository;

import com.worbes.auctionhousetracker.entity.Item;

import java.util.List;

public interface ItemCustomRepository {

    void batchInsertIgnoreConflicts(List<Item> items);
}
