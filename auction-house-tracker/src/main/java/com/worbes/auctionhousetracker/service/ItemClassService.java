package com.worbes.auctionhousetracker.service;


import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;

import java.util.Set;

public interface ItemClassService {

    ItemClass get(Long id);

    void saveRequiredClass(ItemClassesIndexResponse response);

    Set<Long> getMissingItemClasses();
}
