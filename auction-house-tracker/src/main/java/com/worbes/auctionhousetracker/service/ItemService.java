package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.mapper.ItemSaveCommand;
import com.worbes.auctionhousetracker.entity.Item;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ItemService {

    Item get(Long itemId);

    Map<Long, Item> getItemsBy(Collection<Long> itemIds);

    Set<Long> findMissingItemIds(Collection<Long> itemIds);

    void saveAll(List<ItemSaveCommand> responses);
}
