package com.worbes.batch.runner;

import com.worbes.application.item.port.in.CreateItemBonusUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemBonusInitializer {

    private final CreateItemBonusUseCase createItemBonusUseCase;

    @EventListener(ApplicationReadyEvent.class)
    public void importBonuses() {
        createItemBonusUseCase.execute("json/bonuses.json");
    }
}
