package com.worbes.adapter.batch.item;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.CreateItemUseCase;
import com.worbes.application.item.port.in.FetchItemUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class CreateItemWriter implements ItemWriter<Long> {

    private final FetchItemUseCase fetchItemUseCase;
    private final CreateItemUseCase createItemUseCase;

    @Override
    public void write(Chunk<? extends Long> chunk) throws InterruptedException {
        Set<Long> itemIds = new HashSet<>(chunk.getItems());
        try {
            List<Item> items = fetchItemUseCase.fetchItemAsync(itemIds);
            if (!items.isEmpty()) {
                createItemUseCase.saveAll(items);
                log.info("💾 아이템 저장 완료 | 저장된 개수={}", items.size());
            } else {
                log.warn("⚠️ 저장할 아이템이 없음 | 요청된 개수={}", itemIds.size());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("⚠️ ItemWriter 중단됨 | itemIds={}", itemIds);
            throw e;
        }
    }
}
