package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ItemSubclassService {
    void saveRequiredSubclass(List<ItemSubclassResponse> responses, Long itemClass);

    Map<Long, Set<Long>> getMissingItemSubclasses();
}
