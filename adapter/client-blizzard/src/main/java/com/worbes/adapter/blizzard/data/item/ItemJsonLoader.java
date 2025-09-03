package com.worbes.adapter.blizzard.data.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemJsonLoader {

    private final ObjectMapper objectMapper;
    private Map<Long, ItemWowHeadApiResponse> responses;

    @PostConstruct
    public void init() {
        try {
            TypeReference<Map<Long, ItemWowHeadApiResponse>> typeRef = new TypeReference<>() {
            };

            try (var boundStream = new ClassPathResource("json/items.bound.json").getInputStream();
                 var unboundStream = new ClassPathResource("json/items.unbound.json").getInputStream()) {

                responses = objectMapper.readValue(boundStream, typeRef);
                responses.putAll(objectMapper.readValue(unboundStream, typeRef));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<ItemWowHeadApiResponse> fetchAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> responses.get(id));
    }
}
