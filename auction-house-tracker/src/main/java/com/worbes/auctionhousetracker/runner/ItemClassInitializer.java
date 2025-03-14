package com.worbes.auctionhousetracker.runner;

import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.infrastructure.rest.BlizzardApiClient;
import com.worbes.auctionhousetracker.service.ItemClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class ItemClassInitializer implements DataInitializer {

    private final ItemClassService itemClassService;
    private final BlizzardApiClient blizzardApiClient;

    @Override
    public void initialize() {
        if (itemClassService.isRequiredItemClassesExist()) return;
        log.info("아이템 클래스 초기화 시작");

        ItemClassesIndexResponse response = blizzardApiClient.fetchItemClassesIndex();
        itemClassService.save(response);

        log.info("✅ 모든 아이템 클래스 초기화가 완료되었습니다");
    }
}
