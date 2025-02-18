package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.config.properties.RestClientConfigProperties;
import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.oauth2.RestApiClient;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemClassService {
    public static final long ITEM_CLASS_SIZE = 18;
    private final ItemClassRepository itemClassRepository;
    private final RestApiClient restApiClient;
    private final RestClientConfigProperties properties;

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
        String path = properties.getBaseUrlKr() + properties.getItemClassIndexUrl();
        Map<String, String> params = Map.of("namespace", "static-us");
        return restApiClient.get(path, params, ItemClassesIndexResponse.class)
                .getItemClasses()
                .stream()
                .map(ItemClass::new)
                .toList();
    }
}
