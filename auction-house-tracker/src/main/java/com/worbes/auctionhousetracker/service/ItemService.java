package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.mapper.ItemSaveDto;
import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.Item;

import java.util.List;
import java.util.Set;

public interface ItemService {

    Item get(Long itemId);

    Set<Long> findMissingItemIds(AuctionResponse response);

    void save(List<ItemSaveDto> responses);
}
