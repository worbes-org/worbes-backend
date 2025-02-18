package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.config.properties.RestClientConfigProperties;
import com.worbes.auctionhousetracker.dto.response.ItemClassResponse;
import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.oauth2.RestApiClient;
import com.worbes.auctionhousetracker.repository.ItemSubclassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemSubclassService {
    public static final long ITEM_SUBCLASS_SIZE = 152;
    private final ItemSubclassRepository itemSubclassRepository;
    private final RestApiClient restApiClient;
    private final RestClientConfigProperties properties;

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
        String base = properties.getBaseUrlUs();
        String path = String.format(properties.getItemClassUrl(), itemClassId);
        Map<String, String> params = Map.of("namespace", "static-us");
        return restApiClient.get(base + path, params, ItemClassResponse.class)
                .getSubclassResponses()
                .stream()
                .map(ItemClassResponse.Subclass::getId)
                .toList();
    }

    public ItemSubclass fetchItemSubclass(ItemClass itemClass, Long subclassId) {
        String base = properties.getBaseUrlUs();
        Map<String, String> params = Map.of("namespace", "static-us");
        String path = String.format(properties.getItemSubclassUrl(), itemClass.getId(), subclassId);
        return new ItemSubclass(itemClass, restApiClient.get(base + path, params, ItemSubclassResponse.class));
    }
}
