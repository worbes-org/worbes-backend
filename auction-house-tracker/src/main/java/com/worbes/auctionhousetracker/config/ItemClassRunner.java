package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.dto.response.ItemClassResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.entity.embeded.ItemSubclassId;
import com.worbes.auctionhousetracker.service.ItemClassService;
import com.worbes.auctionhousetracker.service.ItemSubclassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ItemClassRunner implements CommandLineRunner {

    private final ItemClassService itemClassService;
    private final ItemSubclassService itemSubclassService;

    public ItemClassRunner(ItemClassService itemClassService, ItemSubclassService itemSubclassService) {
        this.itemClassService = itemClassService;
        this.itemSubclassService = itemSubclassService;
    }

    @Override
    public void run(String... args) {
        if (!itemClassService.getAll().isEmpty()) {
            log.info("아이템 클래스가 이미 저장되어 있습니다.");
            return;
        }

        long startTime = System.nanoTime();
        CompletableFuture.supplyAsync(itemClassService::fetchItemClassesIndex)
                .thenApply(response -> {
                    List<ItemClass> itemClasses = response.getItemClasses().stream().map(ItemClass::new).toList();
                    itemClassService.saveAll(itemClasses);
                    return itemClasses;
                })
                .thenCompose(this::processItemClassesAsync)
                .thenAccept(v -> {
                    log.info("모든 서브 클래스 저장 완료 (총 소요 시간: {} ms)", (System.nanoTime() - startTime) / 1_000_000);
                }).exceptionally(ex -> {
                    log.error("모든 서브 클래스 저장 실패 ", ex);
                    return null;
                });
    }

    private CompletableFuture<Void> processItemClassesAsync(List<ItemClass> itemClasses) {
        List<CompletableFuture<Void>> futures = itemClasses.stream()
                .map(itemClass -> {
                    try {
                        Thread.sleep(100);
                        return fetchAndProcessItemClassAsync(itemClass.getId());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Rate-limiting 작업 중단됨", e);
                    }
                })
                .toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<Void> fetchAndProcessItemClassAsync(Long id) {
        ItemClass itemClass = itemClassService.get(id);
        return CompletableFuture.supplyAsync(() -> itemClassService.fetchItemClass(id))
                .thenCompose(response -> {
                    log.debug("thenCompose 실행: {}", response);
                    return processItemSubclassesAsync(response.getId(), response.getSubclassResponses());
                })
                .thenAccept(v -> {
                    log.info("서브 클래스 저장 완료");
                }).exceptionally(ex -> {
                    throw new RuntimeException(itemClass.getNames().getKo_KR() + " 처리 중 예외 발생", ex);
                });
    }

    private CompletableFuture<Void> processItemSubclassesAsync(Long itemClassId, List<ItemClassResponse.Subclass> subclasses) {
        List<CompletableFuture<Void>> futures = subclasses.stream()
                .map(response -> fetchAndSaveItemSubclassAsync(itemClassId, response.getId()))
                .toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<Void> fetchAndSaveItemSubclassAsync(Long itemClassId, Long itemSubclassId) {
        return CompletableFuture.supplyAsync(() -> itemSubclassService.fetchItemSubclass(itemClassId, itemSubclassId))
                .thenAccept(response -> {
                    ItemSubclass itemSubclass = new ItemSubclass(
                            new ItemSubclassId(itemClassId, response.getId()),
                            itemClassService.get(itemClassId),
                            response.getDisplayName(),
                            response.getVerboseName());
                    itemSubclassService.save(itemSubclass);
                });
    }
}
