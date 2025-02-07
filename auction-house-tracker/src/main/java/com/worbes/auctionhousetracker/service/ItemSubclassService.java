package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.client.BlizzardRestClient;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.repository.ItemSubclassRepository;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.worbes.auctionhousetracker.service.ItemClassService.ITEM_CLASS_SIZE;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemSubclassService {
    private static final int ITEM_SUBCLASS_SIZE = 152;
    private final ItemSubclassRepository itemSubclassRepository;
    private final BlizzardRestClient restClient;
    private final Bucket bucket;

    public Long count() {
        return itemSubclassRepository.count();
    }

    public List<ItemSubclass> getAll() {
        return itemSubclassRepository.findAll();
    }

    public void save(ItemSubclass itemSubclass) {
        itemSubclassRepository.save(itemSubclass);
    }

    public void saveAll(List<ItemSubclass> itemSubclasses) {
        itemSubclassRepository.saveAll(itemSubclasses);
    }

    public void init(List<ItemClass> itemClasses) {
        if (itemClasses.size() != ITEM_CLASS_SIZE) {
            throw new RuntimeException("❌ 아이템 클래스 목록이 비어 있습니다.");
        }

        if(count() == ITEM_SUBCLASS_SIZE) {
            log.info("모든 아이템 서브클래스가 이미 저장되어 있습니다.");
            return;
        }

        List<CompletableFuture<Void>> futures = itemClasses.stream()
                .map(this::processItemClass)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("❌ 아이템 클래스 처리 중 오류 발생: {}", ex.getMessage(), ex);
                        throw new RuntimeException("아이템 클래스 처리 중 오류 발생", ex);
                    }
                    log.info("✅ 모든 아이템 클래스 처리 완료.");
                })
                .join();
    }

    private CompletableFuture<Void> processItemClass(ItemClass itemClass) {
        log.info("▶️ [{}] 서브클래스 ID 조회 시작", itemClass.getNames().getKo_KR());

        return fetchItemSubclassIdsAsync(itemClass.getId())
                .thenCompose(ids -> fetchItemSubclassesAsync(itemClass, ids))
                .thenAccept(this::saveAll)
                .thenRun(() -> log.info("✅ [{}] 서브클래스 저장 완료", itemClass.getNames().getKo_KR()));
    }

    private CompletableFuture<List<Long>> fetchItemSubclassIdsAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            bucket.asBlocking().consumeUninterruptibly(1);
            return restClient.fetchItemSubclassIds(id);
        });
    }

    private CompletableFuture<List<ItemSubclass>> fetchItemSubclassesAsync(ItemClass itemClass, List<Long> ids) {
        List<CompletableFuture<ItemSubclass>> futures = ids.stream()
                .map(subclassId -> CompletableFuture.supplyAsync(() -> {
                    bucket.asBlocking().consumeUninterruptibly(1);
                    return restClient.fetchItemSubclass(itemClass, subclassId);
                }))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList()
                );
    }
}
