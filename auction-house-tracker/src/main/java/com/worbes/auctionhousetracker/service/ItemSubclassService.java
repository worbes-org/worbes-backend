package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.ItemClassResponse;
import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.oauth2.ApiCrawler;
import com.worbes.auctionhousetracker.repository.ItemSubclassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemSubclassService {
    public static final long ITEM_SUBCLASS_SIZE = 152;
    private final ItemSubclassRepository itemSubclassRepository;
    private final ApiCrawler apiCrawler;

    public Long count() {
        return itemSubclassRepository.count();
    }

    public List<ItemSubclass> getAll() {
        return itemSubclassRepository.findAll();
    }

    public void save(ItemSubclass itemSubclass) {
        itemSubclassRepository.save(itemSubclass);
    }

    public void saveAll(List<ItemSubclass> itemSubclasses) {
        itemSubclassRepository.saveAll(itemSubclasses);
    }

    public List<Long> fetchItemSubclassIds(Long itemClassId) {
        return apiCrawler.fetchData(String.format("/item-class/%s", itemClassId), ItemClassResponse.class)
                .getSubclassResponses()
                .stream()
                .map(ItemClassResponse.Subclass::getId)
                .toList();
    }

    public ItemSubclass fetchItemSubclass(ItemClass itemClass, Long subclassId) {
        String path = String.format("/item-class/%s/item-subclass/%s", itemClass.getId(), subclassId);
        return new ItemSubclass(itemClass, apiCrawler.fetchData(path, ItemSubclassResponse.class));
    }
}
