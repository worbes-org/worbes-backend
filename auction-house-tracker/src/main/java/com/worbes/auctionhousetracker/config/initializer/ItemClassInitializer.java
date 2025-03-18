package com.worbes.auctionhousetracker.config.initializer;

import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.infrastructure.rest.BlizzardApiClient;
import com.worbes.auctionhousetracker.service.ItemClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class ItemClassInitializer implements DataInitializer {

    private final ItemClassService itemClassService;
    private final BlizzardApiClient blizzardApiClient;

    @Override
    public void initialize() {
        Set<Long> missingItemClasses = itemClassService.getMissingItemClasses();
        if (missingItemClasses.isEmpty()) {
            log.info("✅ 모든 아이템 클래스가 정상적으로 존재합니다.");
            return;
        }

        log.info("아이템 클래스 초기화 시작");
        ItemClassesIndexResponse response = blizzardApiClient.fetchItemClassesIndex();
        itemClassService.saveRequiredClass(response);

        log.info("✅ 모든 아이템 클래스 초기화가 완료되었습니다");
    }
}
