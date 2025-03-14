package com.worbes.auctionhousetracker.service;


import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;

public interface ItemClassService {

    void save(ItemClassesIndexResponse response);

    boolean isRequiredItemClassesExist();
}
