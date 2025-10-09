package com.worbes.adapter.persistence.jpa.item;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.out.SaveItemPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemJpaAdapter implements SaveItemPort {

    private final ItemJpaRepository jpaRepository;

    @Override
    public void saveAll(List<Item> items) {
        List<ItemEntity> entities = items.stream()
                .map(ItemEntity::from)
                .toList();

        jpaRepository.saveAll(entities);
    }
}
