package com.worbes.adapter.jpa.repository.item;

import com.worbes.adapter.jpa.entity.ItemEntity;
import com.worbes.adapter.jpa.mapper.ItemEntityMapper;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchItemCommand;
import com.worbes.application.item.port.out.CreateItemRepository;
import com.worbes.application.item.port.out.SearchItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements CreateItemRepository, SearchItemRepository {

    private final ItemJpaRepository jpaRepository;
    private final ItemEntityManagerRepository entityManagerRepository;
    private final ItemEntityMapper mapper;

    @Override
    public List<Item> saveAll(List<Item> items) {
        List<ItemEntity> entities = items.stream()
                .map(mapper::toEntity)
                .toList();

        return jpaRepository.saveAll(entities).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Item> searchAll(SearchItemCommand command) {
        return entityManagerRepository.searchAll(command).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
