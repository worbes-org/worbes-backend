package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.config.properties.BlizzardApiConfigProperties;
import com.worbes.auctionhousetracker.dto.response.ItemClassResponse;
import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import com.worbes.auctionhousetracker.repository.ItemSubclassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.STATIC;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemSubclassService {
    public static final long ITEM_SUBCLASS_SIZE = 152;
    private final ItemSubclassRepository itemSubclassRepository;
    private final RestApiClient restApiClient;
    private final BlizzardApiConfigProperties properties;

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
        Region region = Region.US;
        String url = BlizzardApiUrlBuilder.builder(region).itemClass(itemClassId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        return restApiClient.get(url, params, ItemClassResponse.class)
                .getSubclassResponses()
                .stream()
                .map(ItemClassResponse.Subclass::getId)
                .toList();
    }

    public ItemSubclass fetchItemSubclass(ItemClass itemClass, Long subclassId) {
        Region region = Region.US;
        String url = BlizzardApiUrlBuilder.builder(region).itemSubclass(itemClass.getId(), subclassId).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(STATIC).build();
        return new ItemSubclass(itemClass, restApiClient.get(url, params, ItemSubclassResponse.class));
    }
}
