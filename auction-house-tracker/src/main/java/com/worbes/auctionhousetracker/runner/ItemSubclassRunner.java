package com.worbes.auctionhousetracker.runner;


import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.exception.InitializationException;
import com.worbes.auctionhousetracker.service.ItemClassService;
import com.worbes.auctionhousetracker.service.ItemSubclassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.worbes.auctionhousetracker.service.ItemClassService.ITEM_CLASS_SIZE;
import static com.worbes.auctionhousetracker.service.ItemSubclassService.ITEM_SUBCLASS_SIZE;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(2)
public class ItemSubclassRunner implements CommandLineRunner {

    private final ItemClassService itemClassService;
    private final ItemSubclassService itemSubclassService;

    @Override
    public void run(String... args) {
        log.info("▶️ 아이템 서브클래스 초기화 시작");
        if(itemSubclassService.count() == ITEM_SUBCLASS_SIZE) {
            log.info("✅ 모든 아이템 서브클래스가 이미 저장되어 있습니다.");
            return;
        }

        List<ItemClass> itemClasses = itemClassService.getAll();
        if(itemClasses.size() != ITEM_CLASS_SIZE) {
            log.error("❌ 아이템 클래스 개수가 일치하지 않습니다. (현재: {}, 예상: {})", itemClasses.size(), ITEM_CLASS_SIZE);
            throw new InitializationException("아이템 클래스 개수가 일치하지 않습니다.");
        }

        List<CompletableFuture<Void>> futures = itemClasses.stream().map(this::fetchAndSaveItemSubclasses).toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .join();
        log.info("🎉 모든 아이템 서브클래스 초기화가 완료되었습니다.");
    }

    private CompletableFuture<Void> fetchAndSaveItemSubclasses(ItemClass itemClass) {
        return fetchItemSubclassIdsAsync(itemClass.getId())
                .thenApply(ids -> fetchItemSubclassesAsync(itemClass, ids))
                .thenApply(futures -> futures.stream().map(CompletableFuture::join).toList())
                .thenAccept(itemSubclassService::saveAll)
                .thenRun(() -> log.info("✅ [{}] - 모든 서브 클래스 저장 완료", itemClass.getNames().getKo_KR()));
    }

    private CompletableFuture<List<Long>> fetchItemSubclassIdsAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> itemSubclassService.fetchItemSubclassIds(id));
    }

    private List<CompletableFuture<ItemSubclass>> fetchItemSubclassesAsync(ItemClass itemClass, List<Long> subclassIds) {
        return subclassIds.stream()
                .map(subclassId -> CompletableFuture.supplyAsync(() -> itemSubclassService.fetchItemSubclass(itemClass, subclassId)))
                .toList();
    }
}
