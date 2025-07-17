package com.worbes.adapter.jpa.item;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.out.SaveItemPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository implements SaveItemPort {

    private final ItemJpaRepository jpaRepository;
    private final ItemMapper mapper;

    @Override
    public void saveAll(List<Item> items) {
        List<ItemEntity> entities = items.stream()
                .map(mapper::toEntity)
                .toList();

        jpaRepository.saveAll(entities);
    }
}
