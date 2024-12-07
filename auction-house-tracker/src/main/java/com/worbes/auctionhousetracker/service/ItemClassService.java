package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemClassService {

    private final ItemClassRepository itemClassRepository;

    public void saveItemClass(ItemClass itemClass) {
        Optional<ItemClass> existingItemClass = itemClassRepository.findById(itemClass.getId());
        if (existingItemClass.isPresent()) {
            log.info("ItemClass with id " + itemClass.getId() + " already exists.");
            return;
        }
        itemClassRepository.save(itemClass);
    }

    public List<ItemClass> getAllItemClasses() {
        return itemClassRepository.findAll();
    }
}
