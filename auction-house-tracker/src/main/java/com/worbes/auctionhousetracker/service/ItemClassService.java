package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.oauth2.ApiCrawler;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemClassService {
    public static final long ITEM_CLASS_SIZE = 18;
    private final ItemClassRepository itemClassRepository;
    private final ApiCrawler apiCrawler;

    public Long count() {
        return itemClassRepository.count();
    }

    public ItemClass get(Long id) {
        return itemClassRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public List<ItemClass> getAll() {
        return itemClassRepository.findAll();
    }

    public void save(ItemClass itemClass) {
        itemClassRepository.save(itemClass);
    }

    public void saveAll(List<ItemClass> itemClasses) {
        itemClassRepository.saveAll(itemClasses);
    }

    public List<ItemClass> fetchItemClassesIndex() {
        return apiCrawler.fetchData("/item-class/index", ItemClassesIndexResponse.class)
                .getItemClasses()
                .stream()
                .map(ItemClass::new)
                .toList();
    }
}
