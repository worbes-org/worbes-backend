package com.worbes.domain.item.port;

import com.worbes.domain.item.ItemClass;

import java.util.Optional;
import java.util.Set;

public interface ItemClassRepository {
    Optional<ItemClass> findBy(Long id);

    Set<ItemClass> findAllBy(Iterable<Long> classIds);

    void save(ItemClass itemClass);

    void saveAll(Iterable<ItemClass> itemClasses);
}
