package com.worbes.adapter.blizzard.data.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
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
            File bound = new ClassPathResource("/json/items.bound.json").getFile();
            File unbound = new ClassPathResource("/json/items.unbound.json").getFile();
            TypeReference<Map<Long, ItemWowHeadApiResponse>> typeRef = new TypeReference<>() {
            };
            responses = objectMapper.readValue(bound, typeRef);
            responses.putAll(objectMapper.readValue(unbound, typeRef));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<ItemWowHeadApiResponse> fetchAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> responses.get(id));
    }
}
