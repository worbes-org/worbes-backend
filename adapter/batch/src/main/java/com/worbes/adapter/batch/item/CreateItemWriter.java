package com.worbes.adapter.batch.item;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.CreateItemUseCase;
import com.worbes.application.item.port.in.FetchItemApiUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class CreateItemWriter implements ItemWriter<Long> {

    private final FetchItemApiUseCase fetchItemApiUseCase;
    private final CreateItemUseCase createItemUseCase;

    @Override
    public void write(Chunk<? extends Long> chunk) throws InterruptedException {
        if (chunk.isEmpty()) return;
        Set<Long> itemIds = new HashSet<>(chunk.getItems());
        try {
            List<Item> items = fetchItemApiUseCase.fetchItemAsync(itemIds);
            Set<Long> fetchedIds = items.stream().map(Item::getId).collect(Collectors.toSet());
            Set<Long> failedIds = new HashSet<>(itemIds);
            failedIds.removeAll(fetchedIds);

            if (!items.isEmpty()) {
                createItemUseCase.saveAll(items);
                log.info("아이템 저장 완료 | 저장된 개수={}", items.size());
            }
            if (!failedIds.isEmpty()) {
                log.warn("저장 실패 아이템 id: {}", failedIds);
                //TODO: 실패한 아이템 id/사유를 별도 저장
            }
            if (items.isEmpty()) {
                log.warn("저장할 아이템이 없음 | 요청된 개수={}", itemIds.size());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("CreateItemWriter 중단됨 | itemIds={}", itemIds);
            throw e;
        } catch (ExecutionException | TimeoutException e) {
            log.error("CreateItemWriter 예외 발생 | itemIds={} | 이유={}", itemIds, e.getMessage(), e);
            chunk.skip(e);
        }
    }
}
