package com.worbes.jpa.repository;

import com.worbes.domain.item.ItemClass;
import com.worbes.domain.item.port.ItemClassRepository;
import com.worbes.jpa.entity.ItemClassEntity;
import com.worbes.jpa.mapper.ItemClassMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemClassRepositoryImpl implements ItemClassRepository {

    private final ItemClassJpaRepository jpa;

    @Override
    public Optional<ItemClass> findBy(Long id) {
        return jpa.findById(id)
                .map(ItemClassMapper::toDomain);
    }

    @Override
    public List<ItemClass> findAllBy(Iterable<Long> classIds) {
        return jpa.findAllById(classIds)
                .stream()
                .map(ItemClassMapper::toDomain)
                .toList();
    }

    @Override
    public void save(ItemClass itemClass) {
        jpa.save(ItemClassMapper.toEntity(itemClass));
    }

    @Override
    public void saveAll(Iterable<ItemClass> itemClasses) {
        List<ItemClassEntity> entities = new ArrayList<>();
        for (ItemClass itemClass : itemClasses) {
            entities.add(ItemClassMapper.toEntity(itemClass));
        }
        jpa.saveAll(entities);
    }
}
