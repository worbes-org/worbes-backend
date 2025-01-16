package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemClassService {

    private final ItemClassRepository itemClassRepository;

    public ItemClass get(Long id) {
        return itemClassRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public void save(ItemClass itemClass) {
        itemClassRepository.save(itemClass);
    }

    public List<ItemClass> getAll() {
        return itemClassRepository.findAll();
    }
}
