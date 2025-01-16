package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.repository.ItemSubclassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemSubclassService {

    private final ItemSubclassRepository itemSubclassRepository;

    public void save(ItemSubclass itemSubclass) {
        itemSubclassRepository.save(itemSubclass);
    }
}
