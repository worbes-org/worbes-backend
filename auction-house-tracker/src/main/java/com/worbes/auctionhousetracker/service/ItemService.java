package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.entity.Item;

public interface ItemService {

    void saveItem(Item item);

    Item getItem(Long id);

    Item collectItemWithMedia(Long id);

}
