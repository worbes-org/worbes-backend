package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.config.BearerTokenHandler;
import com.worbes.auctionhousetracker.dto.response.ItemClassResponse;
import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemClassService {

    private final ItemClassRepository itemClassRepository;
    private final RestClient restClient;
    private final BearerTokenHandler bearerTokenHandler;

    public ItemClass get(Long id) {
        return itemClassRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public void save(ItemClass itemClass) {
        itemClassRepository.save(itemClass);
    }

    public void saveAll(List<ItemClass> itemClasses) {
        itemClassRepository.saveAll(itemClasses);
    }

    public List<ItemClass> getAll() {
        return itemClassRepository.findAll();
    }

    public ItemClassesIndexResponse fetchItemClassesIndex() {
        return restClient.get()
                .uri(UriComponentsBuilder.fromHttpUrl("https://kr.api.blizzard.com/data/wow/item-class/index")
                        .queryParam("namespace", "static-kr")
                        .queryParam(":region", "kr")
                        .queryParam("locale", "kr")
                        .build()
                        .toUri())
                .header("Authorization", bearerTokenHandler.getToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ItemClassesIndexResponse.class);
    }

    public ItemClassResponse fetchItemClass(Long id) {
        return restClient.get()
                .uri(UriComponentsBuilder.fromHttpUrl("https://kr.api.blizzard.com/data/wow/item-class/{id}")
                        .queryParam("namespace", "static-kr")
                        .queryParam(":region", "kr")
                        .queryParam("locale", "kr")
                        .buildAndExpand(id)
                        .toUri())
                .header("Authorization", bearerTokenHandler.getToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ItemClassResponse.class);
    }
}
