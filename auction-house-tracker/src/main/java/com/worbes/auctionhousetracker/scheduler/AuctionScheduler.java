package com.worbes.auctionhousetracker.scheduler;


import com.worbes.auctionhousetracker.dto.mapper.ItemSaveMapper;
import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.dto.response.ItemMediaResponse;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.BlizzardApiClient;
import com.worbes.auctionhousetracker.service.AuctionService;
import com.worbes.auctionhousetracker.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionService auctionService;
    private final BlizzardApiClient blizzardApiClient;
    private final ItemService itemService;
    private final ThreadPoolTaskScheduler taskScheduler;

    @EventListener(ApplicationReadyEvent.class) // 애플리케이션 준비 완료 후 실행
    public void startScheduledTask() {
        taskScheduler.scheduleAtFixedRate(this::collectAuctionData, Duration.ofHours(1));
    }

    public void collectAuctionData() {
        log.info("🟡 [경매 수집 시작] KR Region Commodities 조회 요청");

        AuctionResponse auctionResponse = blizzardApiClient.fetchCommodities(Region.KR);

        log.info("✅ [경매 데이터 수신 완료] 총 {}건", auctionResponse.getAuctions().size());

        Set<Long> missingItemIds = itemService.findMissingItemIds(auctionResponse).stream()
                .limit(1000)
                .collect(Collectors.toSet());

        log.info("🟠 [미존재 아이템 식별 완료] 신규 아이템 수: {}", missingItemIds.size());
        List<CompletableFuture<ItemMediaResponse>> futures = missingItemIds.stream()
                .map(blizzardApiClient::fetchItemWithMediaAsync)
                .toList();

        log.info("🟡 [아이템 병렬 수집 시작] 요청 수: {}", futures.size());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .map(ItemSaveMapper.INSTANCE::toItemSaveDto)
                        .toList()
                )
                .thenAccept(itemService::save)
                .join();

        auctionService.updateAuctions(auctionResponse, Region.KR);
    }
}
