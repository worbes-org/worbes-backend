package com.worbes.application.item.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.application.item.model.ItemBonus;
import com.worbes.application.item.port.in.CreateItemBonusUseCase;
import com.worbes.application.item.port.out.SaveItemBonusPort;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateItemBonusService implements CreateItemBonusUseCase {

    private final SaveItemBonusPort saveItemBonusPort;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(String filePath) {
        try {
            File file = new ClassPathResource(filePath).getFile();
            TypeReference<Map<String, ItemBonusDto>> typeRef = new TypeReference<>() {
            };
            Map<String, ItemBonusDto> map = objectMapper.readValue(file, typeRef);

            List<ItemBonus> itemBonuses = map.values().stream()
                    .map(b -> new ItemBonus(b.id(), b.name(), b.level(), b.baseLevel()))
                    .filter(itemBonus -> itemBonus.suffix() != null || itemBonus.level() != null || itemBonus.baseLevel() != null)
                    .toList();

            saveItemBonusPort.saveAll(itemBonuses);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ItemBonusDto(
            Long id,
            String name,
            Integer level,
            @JsonProperty("base_level") Integer baseLevel
    ) {
    }
}
