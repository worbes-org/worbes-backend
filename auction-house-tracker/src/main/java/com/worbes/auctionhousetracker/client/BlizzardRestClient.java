package com.worbes.auctionhousetracker.client;

import com.worbes.auctionhousetracker.dto.response.ItemClassResponse;
import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.exception.BlizzardApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class BlizzardRestClient {

    private final RestClient restClient;

    public BlizzardRestClient(@Qualifier("apiClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ItemClass> fetchItemClassesIndex() {
        ItemClassesIndexResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/item-class/index")
                        .queryParam("namespace", "static-kr")
                        .queryParam(":region", "kr")
                        .queryParam("locale", "kr")
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) ->{
                    throw new BlizzardApiException(
                            "Client error while fetching item classes index: " + res.getStatusText(), res.getStatusCode().value()
                    );
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new BlizzardApiException(
                            "Server error while fetching item classes: " + res.getStatusText(),
                            res.getStatusCode().value()
                    );
                })
                .body(ItemClassesIndexResponse.class);

        return Optional.ofNullable(response)
                .map(ItemClassesIndexResponse::getItemClasses)
                .orElseThrow(() -> new BlizzardApiException("Empty response from Blizzard API"))
                .stream()
                .map(ItemClass::new)
                .toList();
    }

    public List<Long> fetchItemSubclassIds(Long id) {
        ItemClassResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/item-class/{id}")
                        .queryParam("namespace", "static-kr")
                        .queryParam(":region", "kr")
                        .queryParam("locale", "kr")
                        .build(id))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new BlizzardApiException(
                            "Client error while fetching item subclasses: " + res.getStatusText(),
                            res.getStatusCode().value()
                    );
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new BlizzardApiException(
                            "Server error while fetching item subclasses: " + res.getStatusText(),
                            res.getStatusCode().value()
                    );
                })
                .body(ItemClassResponse.class);

        return Optional.ofNullable(response)
                .map(ItemClassResponse::getSubclassResponses)
                .orElseThrow(() -> new BlizzardApiException("Empty response from Blizzard API"))
                .stream()
                .map(ItemClassResponse.Subclass::getId)
                .toList();
    }

    public ItemSubclass fetchItemSubclass(ItemClass itemClass, Long itemSubclassId) {
        ItemSubclassResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/item-class/{itemClassId}/item-subclass/{itemSubclassId}")
                        .queryParam("namespace", "static-kr")
                        .queryParam(":region", "kr")
                        .queryParam("locale", "kr")
                        .build(itemClass.getId(), itemSubclassId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new BlizzardApiException(
                            "Client error while fetching item subclass: " + res.getStatusText(),
                            res.getStatusCode().value()
                    );
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new BlizzardApiException(
                            "Server error while fetching item subclass: " + res.getStatusText(),
                            res.getStatusCode().value()
                    );
                })
                .body(ItemSubclassResponse.class);

        return Optional.ofNullable(response)
                .map(res -> new ItemSubclass(itemClass, res))
                .orElseThrow(() -> new BlizzardApiException("Empty response from Blizzard API"));
    }
}
