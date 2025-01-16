package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.config.BearerTokenHandler;
import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.repository.ItemSubclassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemSubclassService {

    private final ItemSubclassRepository itemSubclassRepository;
    private final RestClient restClient;
    private final BearerTokenHandler bearerTokenHandler;

    public void save(ItemSubclass itemSubclass) {
        itemSubclassRepository.save(itemSubclass);
    }

    public ItemSubclassResponse fetchItemSubclass(Long itemClassId, Long itemSubclassId) {
        return restClient.get()
                .uri(UriComponentsBuilder.fromHttpUrl("https://kr.api.blizzard.com/data/wow/item-class/{itemClassId}/item-subclass/{itemSubclassId}")
                        .queryParam("namespace", "static-kr")
                        .queryParam(":region", "kr")
                        .queryParam("locale", "kr")
                        .buildAndExpand(itemClassId, itemSubclassId)
                        .toUri())
                .header("Authorization", bearerTokenHandler.getToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ItemSubclassResponse.class);
    }
}
