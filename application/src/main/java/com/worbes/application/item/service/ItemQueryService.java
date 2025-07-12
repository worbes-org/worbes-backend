package com.worbes.application.item.service;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.GetItemUseCase;
import com.worbes.application.item.port.out.ItemQueryRepository;
import com.worbes.application.item.port.out.ItemSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemQueryService implements GetItemUseCase {

    private final ItemQueryRepository itemQueryRepository;

    @Override
    public Item get(Long itemId) {
        return itemQueryRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }

    @Override
    public List<Item> get(ItemSearchCondition condition) {
        return itemQueryRepository.findByCondition(condition);
    }
}
