package com.worbes.adapter.blizzard.data.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.application.item.port.out.FetchExtraItemInfoPort;
import com.worbes.application.item.port.out.FetchExtraItemInfoResult;
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
public class ItemJsonLoader implements FetchExtraItemInfoPort {

    private final ObjectMapper objectMapper;
    private Map<Long, FetchExtraItemInfoResult> extraItemInfoResults;

    @PostConstruct
    public void init() {
        try {
            File file = new ClassPathResource("/json/items.json").getFile();
            TypeReference<Map<Long, FetchExtraItemInfoResult>> typeRef = new TypeReference<>() {
            };
            extraItemInfoResults = objectMapper.readValue(file, typeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<FetchExtraItemInfoResult> fetchAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> extraItemInfoResults.get(id));
    }
}
