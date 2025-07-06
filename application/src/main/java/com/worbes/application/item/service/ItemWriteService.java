package com.worbes.application.item.service;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.CreateItemUseCase;
import com.worbes.application.item.port.out.ItemWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemWriteService implements CreateItemUseCase {

    private final ItemWriteRepository itemWriteRepository;

    @Override
    public List<Item> saveAll(List<Item> items) {
        if (items == null) {
            throw new IllegalArgumentException("items cannot be null");
        }
        if (items.isEmpty()) {
            return List.of();
        }

        return itemWriteRepository.saveAll(items);
    }
}
