package com.worbes.adapter.batch.item;

import com.worbes.application.item.port.in.CreateItemUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class CreateItemWriter implements ItemWriter<Long> {

    private final CreateItemUseCase createItemUseCase;

    @Override
    public void write(Chunk<? extends Long> chunk) {
        if (chunk.isEmpty()) return;
        Set<Long> itemIds = new HashSet<>(chunk.getItems());
        createItemUseCase.execute(itemIds);
    }
}
