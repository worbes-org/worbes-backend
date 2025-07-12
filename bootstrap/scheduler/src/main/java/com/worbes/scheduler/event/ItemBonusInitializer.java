package com.worbes.scheduler.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.application.bonus.port.model.ItemBonus;
import com.worbes.application.bonus.port.out.BonusCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemBonusInitializer {

    private final ObjectMapper objectMapper;
    private final BonusCommandRepository bonusCommandRepository;

    //    @EventListener(ApplicationReadyEvent.class)
    public void importBonuses() throws IOException {
        // ❶ 읽기
        File file = new ClassPathResource("bonuses.json").getFile();
        TypeReference<Map<String, ItemBonusJson>> typeRef = new TypeReference<>() {
        };
        Map<String, ItemBonusJson> map = objectMapper.readValue(file, typeRef);

        // ❷ 변환
        List<ItemBonus> itemBonuses = map.values().stream()
                .map(b -> new ItemBonus(b.getId(), b.getName(), b.getLevel(), b.getBaseLevel()))
                .filter(itemBonus -> itemBonus.suffix() != null || itemBonus.level() != null || itemBonus.baseLevel() != null)
                .toList();

        bonusCommandRepository.saveAll(itemBonuses);
    }
}
