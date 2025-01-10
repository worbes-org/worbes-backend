package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.dto.response.ItemClassListResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.service.ItemClassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
public class ItemClassDataLoader implements CommandLineRunner {

    private static final String ITEM_CLASS_INDEX_URL = "https://kr.api.blizzard.com/data/wow/item-class/index";
    private final ItemClassService itemClassService;
    private final BearerTokenHandler bearerTokenHandler;

    public ItemClassDataLoader(ItemClassService itemClassService, BearerTokenHandler bearerTokenHandler) {
        this.itemClassService = itemClassService;
        this.bearerTokenHandler = bearerTokenHandler;
    }

    @Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder.fromHttpUrl(ITEM_CLASS_INDEX_URL)
                .queryParam("namespace", "static-kr")
                .queryParam(":region", "kr")
                .queryParam("locale", "kr")
                .build()
                .toUri();

        // Basic Authentication 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerTokenHandler.getToken());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // GET 요청 보내기
            ResponseEntity<ItemClassListResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    ItemClassListResponse.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Error response: {}", response.getStatusCode());
                return;
            }

            if (response.getBody() == null) {
                log.error("response.getBody() is null");
                return;
            }

            ItemClassListResponse responseBody = response.getBody();
            List<ItemClass> collect = responseBody.getItemClasses().stream().map(ItemClass::new).toList();
            collect.forEach(itemClassService::saveItemClass);

        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
    }
}
