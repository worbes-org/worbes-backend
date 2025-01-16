package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.dto.response.ItemClassResponse;
import com.worbes.auctionhousetracker.dto.response.ItemSubclassDetailResponse;
import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.entity.embeded.ItemSubclassId;
import com.worbes.auctionhousetracker.service.ItemClassService;
import com.worbes.auctionhousetracker.service.ItemSubclassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
public class ItemSubclassRunner implements CommandLineRunner {

    private final ItemClassService itemClassService;
    private final ItemSubclassService itemSubclassService;
    private final BearerTokenHandler bearerTokenHandler;


    public ItemSubclassRunner(ItemClassService itemClassService, ItemSubclassService itemSubclassService, BearerTokenHandler bearerTokenHandler) {
        this.itemClassService = itemClassService;
        this.itemSubclassService = itemSubclassService;
        this.bearerTokenHandler = bearerTokenHandler;
    }

    @Override
    public void run(String... args) {
        List<ItemClass> allItemClasses = itemClassService.getAll();
        for (ItemClass allItemClass : allItemClasses) {
            List<ItemSubclassResponse> subclasses = requestSubclasses(allItemClass.getId());
            for (ItemSubclassResponse subclass : subclasses) {
                log.info(subclass.toString());
                ItemSubclassDetailResponse itemSubclassDetailResponse = requestSubclassDetail(allItemClass.getId(), subclass.getId());
                ItemClass itemClass = itemClassService.get(itemSubclassDetailResponse.getClassId());
                ItemSubclass itemSubclass = new ItemSubclass(
                        new ItemSubclassId(itemClass.getId(), itemSubclassDetailResponse.getId()),
                        itemClass,
                        itemSubclassDetailResponse.getDisplayName(),
                        itemSubclassDetailResponse.getVerboseName());
                itemSubclassService.save(itemSubclass);
            }
        }

    }

    private List<ItemSubclassResponse> requestSubclasses(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = UriComponentsBuilder.fromHttpUrl("https://kr.api.blizzard.com/data/wow/item-class/{id}")
                .queryParam("namespace", "static-kr")
                .queryParam(":region", "kr")
                .queryParam("locale", "kr")
                .buildAndExpand(id)
                .toUri();

        // Basic Authentication 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerTokenHandler.getToken());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // GET 요청 보내기
            ResponseEntity<ItemClassResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    ItemClassResponse.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error response: {}" + response.getStatusCode());
            }

            if (response.getBody() == null) {
                log.error("response.getBody() is null");
                throw new RuntimeException("response.getBody() is null");
            }

            return response.getBody().getSubclassResponses();

        } catch (Exception e) {
            throw new RuntimeException("Error occurred: ", e);
        }
    }

    private ItemSubclassDetailResponse requestSubclassDetail(Long itemClassId, Long itemSubclassId) {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = UriComponentsBuilder.fromHttpUrl("https://kr.api.blizzard.com/data/wow/item-class/{itemClassId}/item-subclass/{itemSubclassId}")
                .queryParam("namespace", "static-kr")
                .queryParam(":region", "kr")
                .queryParam("locale", "kr")
                .buildAndExpand(itemClassId, itemSubclassId)
                .toUri();
        log.info(uri.toString());
        // Basic Authentication 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerTokenHandler.getToken());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // GET 요청 보내기
            ResponseEntity<ItemSubclassDetailResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    ItemSubclassDetailResponse.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error response: {}" + response.getStatusCode());
            }

            if (response.getBody() == null) {
                log.error("response.getBody() is null");
                throw new RuntimeException("response.getBody() is null");
            }

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error occurred: ", e);
        }
    }
}
