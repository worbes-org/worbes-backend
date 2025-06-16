package com.worbes.application.item.service;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.CreateItemUseCase;
import com.worbes.application.item.port.out.CreateItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateItemService implements CreateItemUseCase {

    private final CreateItemRepository createItemRepository;

    @Override
    public List<Item> saveAll(List<Item> items) {
        return createItemRepository.saveAll(items);
    }
}
