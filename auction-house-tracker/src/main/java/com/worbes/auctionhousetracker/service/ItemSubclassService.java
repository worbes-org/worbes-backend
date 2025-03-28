package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ItemSubclassService {
    ItemSubclass get(ItemClass itemClass, Long itemSubclassId);

    void saveRequiredSubclass(List<ItemSubclassResponse> responses, Long itemClass);

    Map<Long, Set<Long>> getMissingItemSubclasses();
}
